package com.app.auth.dto.response;

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
@Schema(description = "Authentication response with JWT tokens and user information")
public class AuthResponse {
    @Schema(description = "JWT access token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String accessToken;

    @Schema(description = "JWT refresh token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String refreshToken;

    @Schema(description = "Token type", example = "Bearer")
    private String tokenType = "Bearer";

    @Schema(description = "Token expiration time in seconds", example = "3600")
    private Long expiresIn;

    @Schema(description = "Authenticated user information")
    private UserInfo user;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(description = "User information")
    public static class UserInfo {
        @Schema(description = "User ID", example = "1")
        private Long id;

        @Schema(description = "User email", example = "user@example.com")
        private String email;

        @Schema(description = "User first name", example = "John")
        private String firstName;

        @Schema(description = "User last name", example = "Doe")
        private String lastName;

        @Schema(description = "User roles")
        private Set<UserRole> roles;
    }
}
