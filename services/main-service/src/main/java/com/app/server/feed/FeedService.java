package com.app.server.feed;

import com.app.server.dto.response.FeedResponseDto;
import com.app.server.enums.FriendshipActionType;

public interface FeedService {

    void fanoutNewPost(Long authorId, Long postId, long createdAtEpochMs, String publicity);

    void fanoutFriendActivity(Long actorUserId, Long postId, long activityTimestampMs);

    void removePostFromAllFeeds(Long authorId, Long postId);

    void handleFriendshipChange(Long userId1, Long userId2, FriendshipActionType actionType);

    FeedResponseDto getFeed(Long userId, Long cursorEpochMs, int size);
}