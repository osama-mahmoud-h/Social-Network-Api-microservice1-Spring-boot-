package com.example.server.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AppUserResponseDto {
    private Long id;
    private String username;
    private String email;
    private String image_url;
}
