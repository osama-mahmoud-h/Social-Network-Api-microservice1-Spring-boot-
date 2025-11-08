package com.app.auth.controller;

import com.app.auth.dto.request.*;
import com.app.shared.security.dto.MyApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import com.app.auth.dto.response.AuthResponse;
import com.app.auth.dto.response.ChangePasswordResponse;
import com.app.auth.dto.response.DeviceSessionResponse;
import com.app.auth.dto.response.ForgotPasswordResponse;
import com.app.auth.dto.response.OtpResponse;
import com.app.auth.dto.response.RegistrationResponse;
import com.app.auth.dto.response.ResetPasswordResponse;
import com.app.auth.dto.response.TokenValidationResponse;
import com.app.auth.factory.DeviceInfoFactory;
import com.app.auth.service.AuthService;
import com.app.auth.service.DeviceService;
import com.app.auth.service.OtpService;
import com.app.auth.service.PasswordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication and authorization endpoints")
public class AuthController {

    private final AuthService authService;
    private final AuthenticationManager authenticationManager;
    private final DeviceInfoFactory deviceInfoFactory;
    private final DeviceService deviceService;
    private final OtpService otpService;
    private final PasswordService passwordService;

    @Operation(
            summary = "User registration",
            description = "Registers a new user and sends OTP for email verification"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully registered, OTP sent"),
            @ApiResponse(responseCode = "400", description = "Invalid request or user already exists")
    })
    @PostMapping("/register")
    public ResponseEntity<MyApiResponse<RegistrationResponse>> register(
            @Valid @RequestBody RegisterRequest request) {
        RegistrationResponse registrationResponse = authService.register(request);
        MyApiResponse<RegistrationResponse> response = MyApiResponse.success("User registered successfully. Please verify your email.", registrationResponse);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Verify registration OTP",
            description = "Verifies the OTP sent during registration and completes the registration process, returning JWT tokens"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Email verified successfully, tokens returned"),
            @ApiResponse(responseCode = "400", description = "Invalid or expired OTP")
    })
    @PostMapping("/verify-registration")
    public ResponseEntity<MyApiResponse<AuthResponse>> verifyRegistration(
            @Valid @RequestBody VerifyRegistrationRequest request,
            HttpServletRequest httpRequest) {
        DeviceInfoRequest deviceInfo = deviceInfoFactory.extractDeviceInfo(httpRequest);
        AuthResponse authResponse = authService.verifyRegistration(request, deviceInfo);
        MyApiResponse<AuthResponse> response = MyApiResponse.success("Email verified successfully. You can now login.", authResponse);
        return ResponseEntity.ok(response);
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
    public ResponseEntity<MyApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        DeviceInfoRequest deviceInfo = deviceInfoFactory.extractDeviceInfo(httpRequest);
        AuthResponse authResponse = authService.authenticate(authentication, deviceInfo);
        MyApiResponse<AuthResponse> response = MyApiResponse.success("Login successful", authResponse);
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
    public ResponseEntity<MyApiResponse<TokenValidationResponse>> validateToken(
            @Parameter(description = "JWT token to validate") @RequestParam String token) {
        TokenValidationResponse validationResponse = authService.validateToken(token);
        MyApiResponse<TokenValidationResponse> response = MyApiResponse.success("Token validated", validationResponse);
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
    public ResponseEntity<MyApiResponse<TokenValidationResponse>> validateTokenFromHeader(HttpServletRequest request) {
        String token = extractTokenFromHeader(request);
        if (token == null) {
            TokenValidationResponse validationResponse = TokenValidationResponse.invalid("No token provided");
            MyApiResponse<TokenValidationResponse> response = MyApiResponse.success("Token validated", validationResponse);
            return ResponseEntity.ok(response);
        }
        TokenValidationResponse validationResponse = authService.validateToken(token);
        MyApiResponse<TokenValidationResponse> response = MyApiResponse.success("Token validated", validationResponse);
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
    public ResponseEntity<MyApiResponse<Void>> logout(HttpServletRequest request) {
        String token = extractTokenFromHeader(request);
        if (token != null) {
            authService.logout(token);
        }
        MyApiResponse<Void> response = MyApiResponse.success("Logout successful");
        return ResponseEntity.ok(response);
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
    public ResponseEntity<MyApiResponse<Void>> logoutAllDevices(
            @Parameter(description = "User ID to logout from all devices") @RequestParam Long userId) {
        authService.logoutAllDevices(userId);
        MyApiResponse<Void> response = MyApiResponse.success("Logged out from all devices successfully");
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get user device sessions",
            description = "Returns a list of all active device sessions for the authenticated user"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Device sessions retrieved successfully")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/devices")
    public ResponseEntity<MyApiResponse<List<DeviceSessionResponse>>> getUserDevices(
            @Parameter(description = "User ID") @RequestParam Long userId,
            HttpServletRequest request) {
        String currentToken = extractTokenFromHeader(request);
        List<DeviceSessionResponse> devices = deviceService.getUserDeviceSessions(userId, currentToken);
        MyApiResponse<List<DeviceSessionResponse>> response = MyApiResponse.success("Device sessions retrieved successfully", devices);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Logout from specific device",
            description = "Revokes a specific device session by token ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully logged out from device")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/logout-device")
    public ResponseEntity<MyApiResponse<Void>> logoutDevice(
            @Valid @RequestBody LogoutDeviceRequest request,
            @Parameter(description = "User ID") @RequestParam Long userId) {
        deviceService.logoutDevice(request, userId);
        MyApiResponse<Void> response = MyApiResponse.success("Device logged out successfully");
        return ResponseEntity.ok(response);
    }

//    @Operation(
//            summary = "Send OTP",
//            description = "Sends an OTP code to the specified email address for verification"
//    )
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "OTP sent successfully")
//    })
//    @PostMapping("/send-otp")
//    public ResponseEntity<MyApiResponse<OtpResponse>> sendOtp(
//            @Valid @RequestBody SendOtpRequest request) {
//        OtpResponse otpResponse = otpService.sendOtp(request);
//        MyApiResponse<OtpResponse> response = MyApiResponse.success("OTP sent successfully", otpResponse);
//        return ResponseEntity.ok(response);
//    }

//    @Operation(
//            summary = "Verify OTP",
//            description = "Verifies the OTP code sent to the email address"
//    )
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "OTP verified successfully"),
//            @ApiResponse(responseCode = "400", description = "Invalid or expired OTP")
//    })
//    @PostMapping("/verify-otp")
//    public ResponseEntity<MyApiResponse<OtpResponse>> verifyOtp(
//            @Valid @RequestBody VerifyOtpRequest request) {
//        OtpResponse otpResponse = otpService.verifyOtp(request);
//        MyApiResponse<OtpResponse> response = MyApiResponse.success("OTP verification completed", otpResponse);
//        return ResponseEntity.ok(response);
//    }

    @Operation(
            summary = "Change password",
            description = "Changes the user's password and invalidates all existing sessions"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password changed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid password or password requirements not met")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/change-password")
    public ResponseEntity<MyApiResponse<ChangePasswordResponse>> changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            @Parameter(description = "User ID") @RequestParam Long userId) {
        ChangePasswordResponse changePasswordResponse = passwordService.changePassword(request, userId);
        MyApiResponse<ChangePasswordResponse> response = MyApiResponse.success("Password change processed", changePasswordResponse);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Forgot password",
            description = "Sends a password reset OTP to the user's email address"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password reset OTP sent successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PostMapping("/forgot-password")
    public ResponseEntity<MyApiResponse<ForgotPasswordResponse>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {
        ForgotPasswordResponse forgotPasswordResponse = authService.forgotPassword(request);
        MyApiResponse<ForgotPasswordResponse> response = MyApiResponse.success("Password reset OTP sent successfully", forgotPasswordResponse);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Reset password",
            description = "Resets the user's password using the OTP sent to their email"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password reset successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid OTP or password requirements not met")
    })
    @PostMapping("/reset-password")
    public ResponseEntity<MyApiResponse<ResetPasswordResponse>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {
        ResetPasswordResponse resetPasswordResponse = authService.resetPassword(request);
        MyApiResponse<ResetPasswordResponse> response = MyApiResponse.success("Password reset successfully", resetPasswordResponse);
        return ResponseEntity.ok(response);
    }

    private String extractTokenFromHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
