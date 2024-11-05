package com.example.server.mapper;


import com.example.server.dto.request.post.CreatePostRequestDto;
import com.example.server.dto.response.PostResponseDto;
import com.example.server.dto.response.user.AuthorResponseDto;
import com.example.server.enums.ReactionType;
import com.example.server.model.File;
import com.example.server.model.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostMapper {
    private final FileMapper fileMapper;
    private final UserMapper userMapper;

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
                .author(this.userMapper.mapToAuthorResponseDto(post.getAuthor()))
                .myReactionType(null)
                .reactionsCount(0L)
                .files(post.getFiles().stream()
                        .map(fileMapper::mapFileToFileResponseDto)
                        .collect(Collectors.toSet())
                )
                .build();
    }
}
