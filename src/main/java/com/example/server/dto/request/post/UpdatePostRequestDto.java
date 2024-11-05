package com.example.server.dto.request.post;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdatePostRequestDto {
    @NotNull(message = "Post id cannot be null")
    private Long postId;

    @NotBlank(message = "Post content cannot be blank")
    @Size(min = 1, max = 512, message = "Post content must be between 1 and 512 characters")
    private String content;
}
