package com.app.server.service.impl;

import com.app.server.client.SearchServiceClient;
import com.app.server.dto.response.PostResponseDto;
import com.app.server.dto.response.SearchIdsResponseDto;
import com.app.server.dto.response.SearchResultsResponseDto;
import com.app.server.dto.response.comment.CommentResponseDto;
import com.app.server.enums.SearchEntityType;
import com.app.server.mapper.CommentMapper;
import com.app.server.mapper.PostMapper;
import com.app.server.model.Comment;
import com.app.server.model.Post;
import com.app.server.model.UserProfile;
import com.app.server.repository.CommentRepository;
import com.app.server.repository.PostRepository;
import com.app.server.repository.UserProfileRepository;
import com.app.server.service.SearchOrchestrationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SearchOrchestrationServiceImpl implements SearchOrchestrationService {

    private final SearchServiceClient searchServiceClient;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserProfileRepository userProfileRepository;
    private final PostMapper postMapper;
    private final CommentMapper commentMapper;

    @Override
    public SearchResultsResponseDto<?> search(String searchTerm, SearchEntityType entityType, int size, int page) {
        log.info("Searching for '{}' in entity type: {}", searchTerm, entityType);

        // Step 1: Call search-service to get IDs only
        SearchIdsResponseDto searchIdsResponse;
        try {
            searchIdsResponse = searchServiceClient.searchIds(searchTerm, entityType.toString(), size, page);
        } catch (Exception e) {
            log.error("Failed to call search-service: {}", e.getMessage(), e);
            throw new RuntimeException("Search service unavailable", e);
        }

        if (searchIdsResponse.getIds() == null || searchIdsResponse.getIds().isEmpty()) {
            log.info("No results found for search term: {}", searchTerm);
            return buildEmptyResponse(entityType.toString(), page, size);
        }

        // Step 2: Fetch full entities from PostgreSQL based on IDs
        List<Long> ids = searchIdsResponse.getIds();
        log.info("Found {} IDs from search-service", ids.size());

        return switch (entityType) {
            case POST_INDEX -> fetchPosts(ids, entityType.toString(), page, size);
            case COMMENT_INDEX -> fetchComments(ids, entityType.toString(), page, size);
            case USER_INDEX -> fetchUsers(ids, entityType.toString(), page, size);
            default -> throw new IllegalArgumentException("Unsupported entity type: " + entityType);
        };
    }

    private SearchResultsResponseDto<PostResponseDto> fetchPosts(List<Long> postIds, String entityType, int page, int size) {
        // write pagination logic.
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        List<Post> posts = postRepository.getPostsByIds(postIds, pageable);
        log.info("Fetched {} posts from database", posts.size());
        return SearchResultsResponseDto.<PostResponseDto>builder()
                .entityType(entityType)
                .results(posts
                        .stream()
                        .map(postMapper::mapPostToPostResponseDto)
                        .toList())
                .totalResults(posts.size())
                .page(page)
                .size(size)
                .build();
    }

    private SearchResultsResponseDto<CommentResponseDto> fetchComments(List<Long> commentIds, String entityType, int page, int size) {
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        List<Comment> comments = commentRepository.getAllByIds(commentIds, pageable);
        log.info("Fetched {} comments from database", comments.size());

        return SearchResultsResponseDto.<CommentResponseDto>builder()
                .entityType(entityType)
                .results(comments
                        .stream()
                        .map(commentMapper::mapCommentToCommentResponseDto)
                        .toList()
                )
                .totalResults(comments.size())
                .page(page)
                .size(size)
                .build();
    }

    private SearchResultsResponseDto<UserProfile> fetchUsers(List<Long> userIds, String entityType, int page, int size) {
        List<UserProfile> users = userProfileRepository.findAllById(userIds);
        log.info("Fetched {} users from database", users.size());

        return SearchResultsResponseDto.<UserProfile>builder()
                .entityType(entityType)
                .results(users)
                .totalResults(users.size())
                .page(page)
                .size(size)
                .build();
    }

    private SearchResultsResponseDto<?> buildEmptyResponse(String entityType, int page, int size) {
        return SearchResultsResponseDto.builder()
                .entityType(entityType)
                .results(Collections.emptyList())
                .totalResults(0)
                .page(page)
                .size(size)
                .build();
    }
}

