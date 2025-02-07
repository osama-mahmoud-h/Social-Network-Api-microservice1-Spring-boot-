package com.app.server.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
public class CommentRequestDto {
    @NotBlank(message = "post not found")
    private Long post_id;

    @NotBlank(message = "this field Cannot be empty")
    private String text;
}
