package com.app.server.mapper;


import com.app.server.dto.request.post.CreatePostRequestDto;
import com.app.server.dto.response.FileResponseDto;
import com.app.server.dto.response.PostResponseDto;
import com.app.server.dto.response.user.AuthorResponseDto;
import com.app.server.enums.ReactionType;
import com.app.server.model.Post;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostMapper {
    private final FileMapper fileMapper;
    private final UserMapper userMapper;
    private final ObjectMapper objectMapper;

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

    public PostResponseDto mapDbRowToPostResponseDto(Object[] row) {
        try {
            return PostResponseDto.builder()
                    .postId(((Number) row[0]).longValue())
                    .content((String) row[1])
                    .commentsCount(((Number) row[2]).longValue())
                    .reactionsCount(((Number) row[3]).longValue())
                    .createdAt(((Instant) row[4]))
                    .updatedAt(((Instant) row[5]))
                    .author(objectMapper.readValue((String) row[6], AuthorResponseDto.class)) // Parse JSON to AuthorResponseDto
                    .myReactionType(row[7] != null ? ReactionType.valueOf((String) row[7]) : null)
                    .files(parseFilesJson((String) row[8])) // Parse JSON to Set<FileResponseDto>
                    .build();
        } catch (Exception e) {
            log.error("Failed to map row to PostResponseDto", e);
            throw new RuntimeException("Failed to map row to PostResponseDto", e);
        }
    }

    private Set<FileResponseDto> parseFilesJson(String filesJson) {
        try {
            List<FileResponseDto> files = objectMapper.readValue(filesJson, new TypeReference<List<FileResponseDto>>() {});
            return new HashSet<>(files); // Convert List to Set as required by PostResponseDto
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse files JSON", e);
        }
    }
}
