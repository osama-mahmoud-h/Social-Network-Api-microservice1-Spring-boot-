package com.app.server.dto.request.post;


import com.app.server.enums.PostPublicity;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class CreatePostRequestDto {
    @NotBlank(message = "content is required")
    private String content;

    private PostPublicity publicity = PostPublicity.PUBLIC;

    private MultipartFile[] files;
}
