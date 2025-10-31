package com.app.server.service.impl;

import com.app.server.dto.response.PostResponseDto;
import com.app.server.enums.ReactionType;
import com.app.server.mapper.FileMapper;
import com.app.server.mapper.UserMapper;
import com.app.server.model.Post;
import com.app.server.repository.CommentRepository;
import com.app.server.repository.PostRepository;
import com.app.server.repository.PostRepositoryCustom;
import com.app.server.repository.UserReactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

/**
 * Example service showing how to fetch post details with separate queries
 * This gives you actual File entities + aggregation counts
 */
@Service
@RequiredArgsConstructor
public class PostDetailService {

    private final PostRepository postRepository;
    private final PostRepositoryCustom postRepositoryCustom;
    private final CommentRepository commentRepository;
    private final UserReactionRepository userReactionRepository;
    private final FileMapper fileMapper;
    private final UserMapper userMapper;

    /**
     * Get post details with ACTUAL file entities (not JSON)
     * Plus all the counts and user-specific data
     */
    public PostResponseDto getPostDetails(Long userId, Long postId) {
        // 1. Fetch post with files (actual File entities!)
        Post post = postRepositoryCustom.findPostWithFiles(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        // 2. Get aggregation counts (separate queries - but fast!)
        Long commentsCount = commentRepository.countByPostId(postId);
        Long reactionsCount = userReactionRepository.countByTargetId(postId);

        // 3. Get user's reaction (if any)
        ReactionType myReaction = userReactionRepository
                .findByAuthorIdAndTargetId(userId, postId)
                .map(reaction -> reaction.getReactionType())
                .orElse(null);

        // 4. Build DTO - Files are already Set<File>!
        return PostResponseDto.builder()
                .postId(post.getPostId())
                .content(post.getContent())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .author(userMapper.mapToAuthorResponseDto(post.getAuthor()))
                .files(post.getFiles().stream()
                        .map(fileMapper::mapFileToFileResponseDto)  // ✅ Map actual entities!
                        .collect(Collectors.toSet()))
                .commentsCount(commentsCount)
                .reactionsCount(reactionsCount)
                .myReactionType(myReaction)
                .build();
    }
}