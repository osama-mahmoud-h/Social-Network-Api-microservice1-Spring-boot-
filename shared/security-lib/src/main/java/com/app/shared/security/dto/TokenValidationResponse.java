package com.app.shared.security.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TokenValidationResponse {
    private boolean valid;
    private Long userId;
    private String email;
    private Set<String> roles;
    private String message;

    public static TokenValidationResponse invalid(String message) {
        return TokenValidationResponse.builder()
                .valid(false)
                .message(message)
                .build();
    }

    public static TokenValidationResponse valid(Long userId, String email, Set<String> roles) {
        return TokenValidationResponse.builder()
                .valid(true)
                .userId(userId)
                .email(email)
                .roles(roles)
                .build();
    }
}
