package com.app.server.dto.response.comment;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponseDto {
    private Long commentId;
    private Long parentId;
    private String content;
    private Long postId;
    private Long authorId;
    private Set<CommentResponseDto> replies;
}
