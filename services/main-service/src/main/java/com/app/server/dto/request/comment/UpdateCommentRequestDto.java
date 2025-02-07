package com.app.server.dto.request.comment;

import lombok.Data;

@Data
public class UpdateCommentRequestDto {
    private Long commentId;
    private String content;
}
