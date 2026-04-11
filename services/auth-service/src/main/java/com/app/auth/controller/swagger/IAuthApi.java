package com.app.auth.controller.swagger;

import com.app.auth.config.swagger.AutoValidationExamples;
import com.app.auth.dto.request.*;
import com.app.auth.dto.response.*;
import com.app.shared.security.dto.MyApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    // TODO: add @Operation + @ApiResponses for remaining methods

    ResponseEntity<MyApiResponse<AuthResponse>> verifyRegistration(
            @Valid @RequestBody VerifyRegistrationRequest request,
            HttpServletRequest httpRequest);

    ResponseEntity<MyApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest);

    ResponseEntity<MyApiResponse<TokenValidationResponse>> validateToken(
            @Parameter(description = "JWT token to validate") @RequestParam String token);

    ResponseEntity<MyApiResponse<TokenValidationResponse>> validateTokenFromHeader(HttpServletRequest request);

    ResponseEntity<MyApiResponse<Void>> logout(HttpServletRequest request);

    ResponseEntity<MyApiResponse<Void>> logoutAllDevices(
            @Parameter(description = "User ID to logout from all devices") @RequestParam Long userId);

    ResponseEntity<MyApiResponse<List<DeviceSessionResponse>>> getUserDevices(
            @Parameter(description = "User ID") @RequestParam Long userId,
            HttpServletRequest request);

    ResponseEntity<MyApiResponse<Void>> logoutDevice(
            @Valid @RequestBody LogoutDeviceRequest request,
            @Parameter(description = "User ID") @RequestParam Long userId);

    ResponseEntity<MyApiResponse<ChangePasswordResponse>> changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            @Parameter(description = "User ID") @RequestParam Long userId);

    ResponseEntity<MyApiResponse<ForgotPasswordResponse>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request);

    ResponseEntity<MyApiResponse<ResetPasswordResponse>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request);
}
