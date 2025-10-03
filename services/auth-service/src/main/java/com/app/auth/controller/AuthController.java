package com.app.auth.controller;

import com.app.auth.dto.AuthResponse;
import com.app.auth.dto.LoginRequest;
import com.app.auth.dto.RegisterRequest;
import com.app.auth.dto.TokenValidationResponse;
import com.app.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication and authorization endpoints")
public class AuthController {

    private final AuthService authService;
    private final AuthenticationManager authenticationManager;

    @Operation(
            summary = "User registration",
            description = "Registers a new user and returns JWT tokens"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully registered"),
            @ApiResponse(responseCode = "400", description = "Invalid request or user already exists")
    })
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest request) {
        try {
            AuthResponse response = authService.register(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(
            summary = "User login",
            description = "Authenticates a user with email and password and returns JWT tokens"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully authenticated"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        AuthResponse response = authService.authenticate(authentication);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Validate JWT token",
            description = "Validates a JWT token and returns user information if valid"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token validation result returned")
    })
    @PostMapping("/validate")
    public ResponseEntity<TokenValidationResponse> validateToken(
            @Parameter(description = "JWT token to validate") @RequestParam String token) {
        TokenValidationResponse response = authService.validateToken(token);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Validate JWT token from Authorization header",
            description = "Validates a JWT token from the Authorization header"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token validation result returned")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/validate-header")
    public ResponseEntity<TokenValidationResponse> validateTokenFromHeader(HttpServletRequest request) {
        String token = extractTokenFromHeader(request);
        if (token == null) {
            return ResponseEntity.ok(TokenValidationResponse.invalid("No token provided"));
        }
        TokenValidationResponse response = authService.validateToken(token);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "User logout",
            description = "Logs out the current user by revoking their access token"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully logged out")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        String token = extractTokenFromHeader(request);
        if (token != null) {
            authService.logout(token);
        }
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Logout from all devices",
            description = "Revokes all tokens for a specific user, logging them out from all devices"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully logged out from all devices")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/logout-all")
    public ResponseEntity<Void> logoutAllDevices(
            @Parameter(description = "User ID to logout from all devices") @RequestParam Long userId) {
        authService.logoutAllDevices(userId);
        return ResponseEntity.ok().build();
    }

    private String extractTokenFromHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
