package com.example.server.mapper;


import com.example.server.dto.request.post.CreatePostRequestDto;
import com.example.server.dto.response.PostResponseDto;
import com.example.server.model.File;
import com.example.server.model.Post;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class PostMapper {
    public Post mapCreatePostRequestDtoToPost(CreatePostRequestDto createPostRequestDto) {
        return Post.builder()
                .content(createPostRequestDto.getContent())
                .files(null)
                .author(null)
                .createdAt(Instant.now())
                .build();
    }

    public PostResponseDto mapPostToPostResponseDto(Post post) {
        return PostResponseDto.builder()
                .postId(post.getPostId())
                .content(post.getContent())
                .commentsCount(0L)
                .author(null)
                .myReactionType(null)
                .userReactions(null)
                .build();
    }
}
