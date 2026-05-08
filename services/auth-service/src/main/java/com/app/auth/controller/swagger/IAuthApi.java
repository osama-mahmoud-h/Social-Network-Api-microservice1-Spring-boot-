package com.app.auth.controller.swagger;

import com.app.auth.config.swagger.AutoValidationExamples;
import com.app.auth.dto.request.*;
import com.app.auth.dto.response.*;
import com.app.shared.security.dto.MyApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "Authentication", description = "Authentication and authorization endpoints")
public interface IAuthApi {

    @Operation(
            summary = "User registration",
            description = "Registers a new user and sends OTP for email verification"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully registered, OTP sent"),
            @ApiResponse(responseCode = "400", description = "Invalid request or user already exists")
    })
    @AutoValidationExamples
    ResponseEntity<MyApiResponse<RegistrationResponse>> register(
            @Valid @RequestBody RegisterRequest request);

    @Operation(
            summary = "Verify registration OTP",
            description = "Verifies the OTP sent during registration and completes the registration process, returning JWT tokens"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Email verified successfully, tokens returned"),
            @ApiResponse(responseCode = "400", description = "Invalid or expired OTP")
    })
    ResponseEntity<MyApiResponse<AuthResponse>> verifyRegistration(
            @Valid @RequestBody VerifyRegistrationRequest request,
            HttpServletRequest httpRequest);

    @Operation(
            summary = "User login",
            description = "Authenticates a user with email and password and returns JWT tokens"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully authenticated"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    ResponseEntity<MyApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest);

    @Operation(
            summary = "Validate JWT token",
            description = "Validates a JWT token and returns user information if valid"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token validation result returned")
    })
    ResponseEntity<MyApiResponse<TokenValidationResponse>> validateToken(
            @Parameter(description = "JWT token to validate") @RequestParam String token);

    @Operation(
            summary = "Validate JWT token from Authorization header",
            description = "Validates a JWT token from the Authorization header"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token validation result returned")
    })
    @SecurityRequirement(name = "bearerAuth")
    ResponseEntity<MyApiResponse<TokenValidationResponse>> validateTokenFromHeader(HttpServletRequest request);

    @Operation(
            summary = "User logout",
            description = "Logs out the current user by revoking their access token"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully logged out")
    })
    @SecurityRequirement(name = "bearerAuth")
    ResponseEntity<MyApiResponse<Void>> logout(HttpServletRequest request);

    @Operation(
            summary = "Logout from all devices",
            description = "Revokes all tokens for a specific user, logging them out from all devices"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully logged out from all devices")
    })
    @SecurityRequirement(name = "bearerAuth")
    ResponseEntity<MyApiResponse<Void>> logoutAllDevices(
            @Parameter(description = "User ID to logout from all devices") @RequestParam Long userId);

    @Operation(
            summary = "Get user device sessions",
            description = "Returns a list of all active device sessions for the authenticated user"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Device sessions retrieved successfully")
    })
    @SecurityRequirement(name = "bearerAuth")
    ResponseEntity<MyApiResponse<List<DeviceSessionResponse>>> getUserDevices(
            @Parameter(description = "User ID") @RequestParam Long userId,
            HttpServletRequest request);

    @Operation(
            summary = "Logout from specific device",
            description = "Revokes a specific device session by token ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully logged out from device")
    })
    @SecurityRequirement(name = "bearerAuth")
    ResponseEntity<MyApiResponse<Void>> logoutDevice(
            @Valid @RequestBody LogoutDeviceRequest request,
            @Parameter(description = "User ID") @RequestParam Long userId);

    @Operation(
            summary = "Change password",
            description = "Changes the user's password and invalidates all existing sessions"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password changed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid password or password requirements not met")
    })
    @SecurityRequirement(name = "bearerAuth")
    ResponseEntity<MyApiResponse<ChangePasswordResponse>> changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            @Parameter(description = "User ID") @RequestParam Long userId);

    @Operation(
            summary = "Forgot password",
            description = "Sends a password reset OTP to the user's email address"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password reset OTP sent successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    ResponseEntity<MyApiResponse<ForgotPasswordResponse>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request);

    @Operation(
            summary = "Reset password",
            description = "Resets the user's password using the OTP sent to their email"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password reset successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid OTP or password requirements not met")
    })
    ResponseEntity<MyApiResponse<ResetPasswordResponse>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request);
}