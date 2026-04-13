package com.app.server.feed;

import com.app.server.dto.response.FeedResponseDto;
import com.app.server.dto.response.PostResponseDto;
import com.app.server.enums.FriendshipActionType;
import com.app.server.enums.PostPublicity;
import com.app.server.mapper.PostMapper;
import com.app.server.projection.PostDetailProjection;
import com.app.server.repository.FriendshipServiceRepository;
import com.app.server.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeedServiceImpl implements FeedService {

    private static final String FEED_KEY_PREFIX = "feed:";
    private static final int MAX_FEED_SIZE = 500;
    private static final int BACKFILL_POST_LIMIT = 20;

    private final RedisTemplate<String, String> redisTemplate;
    private final FriendshipServiceRepository friendshipRepository;
    private final PostRepository postRepository;
    private final PostMapper postMapper;

    @Override
    public void fanoutNewPost(Long authorId, Long postId, long createdAtEpochMs, String publicity) {
        if (PostPublicity.PRIVATE.name().equals(publicity)) {
            return;
        }

        List<Long> friendIds = friendshipRepository.findAcceptedFriendIds(authorId);

        addPostToFeed(authorId, postId, createdAtEpochMs);

        friendIds.forEach(friendId -> addPostToFeed(friendId, postId, createdAtEpochMs));

        log.debug("Fanned out postId={} to {} friends of authorId={}", postId, friendIds.size(), authorId);
    }

    @Override
    public void fanoutFriendActivity(Long actorUserId, Long postId, long activityTimestampMs) {
        List<Long> actorFriendIds = friendshipRepository.findAcceptedFriendIds(actorUserId);

        actorFriendIds.forEach(friendId -> {
            String feedKey = feedKeyFor(friendId);
            Double existingScore = redisTemplate.opsForZSet().score(feedKey, String.valueOf(postId));
            if (existingScore == null) {
                redisTemplate.opsForZSet().add(feedKey, String.valueOf(postId), activityTimestampMs);
                trimFeedToMaxSize(feedKey);
            }
        });

        log.debug("Fanned out friend activity: actorId={}, postId={}, reached {} friends",
                actorUserId, postId, actorFriendIds.size());
    }

    @Override
    public void removePostFromAllFeeds(Long authorId, Long postId) {
        List<Long> friendIds = friendshipRepository.findAcceptedFriendIds(authorId);

        String postMember = String.valueOf(postId);
        redisTemplate.opsForZSet().remove(feedKeyFor(authorId), postMember);
        friendIds.forEach(friendId ->
                redisTemplate.opsForZSet().remove(feedKeyFor(friendId), postMember));

        log.debug("Removed postId={} from {} feeds", postId, friendIds.size() + 1);
    }

    @Override
    public void handleFriendshipChange(Long userId1, Long userId2, FriendshipActionType actionType) {
        if (actionType == FriendshipActionType.ACCEPTED) {
            backfillRecentPostsIntoFeed(userId1, userId2);
            backfillRecentPostsIntoFeed(userId2, userId1);
        } else {
            removeAllPostsByUserFromFeed(userId1, userId2);
            removeAllPostsByUserFromFeed(userId2, userId1);
        }
    }

    @Override
    public FeedResponseDto getFeed(Long userId, Long cursorEpochMs, int size) {
        List<Long> postIds = fetchPostIdsFromCache(userId, cursorEpochMs, size);

        log.debug("Cache hit for userId={}, postIds={}", userId, postIds);

        List<PostResponseDto> posts = postIds.isEmpty()
                ? fetchFromDatabaseAndBackfillCache(userId, cursorEpochMs, size)
                : hydratePostIds(userId, postIds);
        log.debug("Fetched {} posts for userId={}", posts.size(), userId);

        Long nextCursor = posts.isEmpty() ? null
                : posts.get(posts.size() - 1).getCreatedAt().toEpochMilli();

        return FeedResponseDto.builder()
                .posts(posts)
                .nextCursor(nextCursor)
                .hasMore(posts.size() == size)
                .build();
    }

    private List<Long> fetchPostIdsFromCache(Long userId, Long cursorEpochMs, int size) {
        double maxScore = cursorEpochMs != null ? cursorEpochMs - 1 : Double.MAX_VALUE;

        Set<String> members = redisTemplate.opsForZSet()
                .reverseRangeByScore(feedKeyFor(userId), Double.MIN_VALUE, maxScore, 0, size);
        log.debug("Cache hit for userId={}, members={}", userId, members);

        if (members == null || members.isEmpty()) {
            return Collections.emptyList();
        }

        return members.stream().map(Long::parseLong).collect(Collectors.toList());
    }

    private List<PostResponseDto> fetchFromDatabaseAndBackfillCache(Long userId, Long cursorEpochMs, int size) {
        log.debug("Cache miss for userId={}, falling back to database", userId);

        List<PostDetailProjection> projections = postRepository.findFriendsFeedWithCursor(userId, cursorEpochMs, size);

        projections.forEach(p -> addPostToFeed(userId, p.getPostId(),
                p.getCreatedAt().toEpochMilli()));

        return projections.stream()
                .map(postMapper::mapProjectionToPostResponseDto)
                .collect(Collectors.toList());
    }

    private List<PostResponseDto> hydratePostIds(Long userId, List<Long> postIds) {
        return postRepository.findPostDetailsByIds(userId, postIds).stream()
                .map(postMapper::mapProjectionToPostResponseDto)
                .collect(Collectors.toList());
    }

    private void addPostToFeed(Long userId, Long postId, long scoreMs) {
        String feedKey = feedKeyFor(userId);
        redisTemplate.opsForZSet().add(feedKey, String.valueOf(postId), scoreMs);
        trimFeedToMaxSize(feedKey);
    }

    private void trimFeedToMaxSize(String feedKey) {
        redisTemplate.opsForZSet().removeRange(feedKey, 0, -(MAX_FEED_SIZE + 1));
    }

    private void backfillRecentPostsIntoFeed(Long beneficiaryUserId, Long newFriendId) {
        postRepository.findUserPosts(newFriendId, PageRequest.of(0, BACKFILL_POST_LIMIT))
                .stream()
                .filter(post -> post.getPublicity() != PostPublicity.PRIVATE)
                .forEach(post -> addPostToFeed(beneficiaryUserId, post.getPostId(),
                        post.getCreatedAt().toEpochMilli()));
    }

    private void removeAllPostsByUserFromFeed(Long feedOwnerUserId, Long removedFriendId) {
        postRepository.findUserPosts(removedFriendId, PageRequest.of(0, MAX_FEED_SIZE))
                .forEach(post -> redisTemplate.opsForZSet()
                        .remove(feedKeyFor(feedOwnerUserId), String.valueOf(post.getPostId())));
    }

    private String feedKeyFor(Long userId) {
        return FEED_KEY_PREFIX + userId;
    }
}