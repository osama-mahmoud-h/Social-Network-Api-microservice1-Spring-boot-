package com.example.server.dto.request.post;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class CreatePostRequestDto {
    @NotBlank(message = "content is required")
    private String content;

    private MultipartFile[] files;
}
