package com.example.server.dto.response.user;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class AuthorResponseDto {
    private Long userId;
    private String firstName;
    private String lastName;
    private String email;
    private String profilePictureUrl;
    private String bio;

}
