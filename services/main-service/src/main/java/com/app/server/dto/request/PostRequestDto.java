package com.app.server.dto.request;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class PostRequestDto {
        private MultipartFile[] images;
        private MultipartFile video;
        private MultipartFile file;
        private String text;
}
