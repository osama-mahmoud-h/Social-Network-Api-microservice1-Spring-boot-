package com.example.server.payload.request;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
public class CommentRequestDto {
    @NotBlank(message = "post not found")
    private Long post_id;

    @NotBlank(message = "this field Cannot be empty")
    private String text;
}
