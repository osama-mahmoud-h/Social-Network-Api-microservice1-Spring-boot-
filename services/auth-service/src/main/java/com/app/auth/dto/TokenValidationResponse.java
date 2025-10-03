package com.app.auth.dto;

import com.app.auth.enums.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Token validation response")
public class TokenValidationResponse {
    @Schema(description = "Whether the token is valid", example = "true")
    private boolean valid;

    @Schema(description = "User ID (if valid)", example = "1")
    private Long userId;

    @Schema(description = "User email (if valid)", example = "user@example.com")
    private String email;

    @Schema(description = "User roles (if valid)")
    private Set<UserRole> roles;

    @Schema(description = "Validation message (if invalid)", example = "Token is expired")
    private String message;

    public static TokenValidationResponse invalid(String message) {
        return TokenValidationResponse.builder()
                .valid(false)
                .message(message)
                .build();
    }

    public static TokenValidationResponse valid(Long userId, String email, Set<UserRole> roles) {
        return TokenValidationResponse.builder()
                .valid(true)
                .userId(userId)
                .email(email)
                .roles(roles)
                .build();
    }
}
