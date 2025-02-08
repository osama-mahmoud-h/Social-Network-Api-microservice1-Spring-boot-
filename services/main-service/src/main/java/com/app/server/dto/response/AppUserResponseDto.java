package com.app.server.dto.response;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class AppUserResponseDto {
    private Long userId;
    private String username;
    private String email;
    private String image_url;
}
