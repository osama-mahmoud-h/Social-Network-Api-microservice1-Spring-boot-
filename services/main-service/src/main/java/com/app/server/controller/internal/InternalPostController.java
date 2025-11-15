package com.app.server.controller.internal;

import com.app.server.dto.internal.PostInfoDto;
import com.app.server.model.Post;
import com.app.server.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Internal API endpoints for post-related inter-service communication
 *
 * @deprecated This controller is deprecated as of the event-driven refactoring.
 * Post information is now included directly in Kafka events (PostEventDto, CommentEventDto),
 * eliminating the need for synchronous HTTP calls between services.
 * This endpoint may be removed in a future version.
 *
 * Note: In production, this should be secured with service-to-service authentication
 * or exposed only through internal network/API gateway
 */
@Deprecated
@RestController
@RequestMapping("/internal/api/posts")
@RequiredArgsConstructor
@Slf4j
public class InternalPostController {

    private final PostRepository postRepository;

    /**
     * Get simplified post information by post ID
     *
     * @deprecated No longer needed. Post author information is now included
     * in CommentEventDto.postAuthorId field, eliminating this synchronous call.
     * This endpoint will be removed in a future version.
     *
     * @param postId The post ID to fetch
     * @return Post information including author ID
     */
    @Deprecated
    @GetMapping("/{postId}")
    public ResponseEntity<PostInfoDto> getPostById(@PathVariable Long postId) {
        log.debug("Internal API: Fetching post info for postId={}", postId);

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found: " + postId));

        PostInfoDto postInfo = PostInfoDto.builder()
                .postId(post.getPostId())
                .authorId(post.getAuthor().getUserId())
                .content(post.getContent())
                .build();

        log.debug("Internal API: Retrieved post info for postId={}, authorId={}",
                postId, postInfo.getAuthorId());

        return ResponseEntity.ok(postInfo);
    }
}