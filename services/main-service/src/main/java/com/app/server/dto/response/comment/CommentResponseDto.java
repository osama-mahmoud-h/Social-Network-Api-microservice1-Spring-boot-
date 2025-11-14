package com.app.server.dto.response.comment;


import com.app.server.dto.response.user.AuthorResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponseDto {
    private Long commentId;
    private Long parentId;
    private String content;
    private Instant createdAt;
    private Instant updatedAt;
    private Long postId;
    private AuthorResponseDto author;
    private Set<CommentResponseDto> replies;
}
