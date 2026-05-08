package com.app.auth.controller;

import com.app.auth.controller.swagger.IAuthApi;
import com.app.auth.dto.request.*;
import com.app.shared.security.dto.MyApiResponse;
import com.app.auth.dto.response.*;
import com.app.auth.factory.DeviceInfoFactory;
import com.app.auth.service.AuthService;
import com.app.auth.service.DeviceService;
import com.app.auth.service.OtpService;
import com.app.auth.service.PasswordService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController implements IAuthApi {

    private final AuthService authService;
    private final AuthenticationManager authenticationManager;
    private final DeviceInfoFactory deviceInfoFactory;
    private final DeviceService deviceService;
    private final OtpService otpService;
    private final PasswordService passwordService;

    @Override
    @PostMapping("/register")
    public ResponseEntity<MyApiResponse<RegistrationResponse>> register(
            @Valid @RequestBody RegisterRequest request) {
        RegistrationResponse registrationResponse = authService.register(request);
        MyApiResponse<RegistrationResponse> response = MyApiResponse.success(
                "User registered successfully. Please verify your email.", registrationResponse);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    @PostMapping("/verify-registration")
    public ResponseEntity<MyApiResponse<AuthResponse>> verifyRegistration(
            @Valid @RequestBody VerifyRegistrationRequest request,
            HttpServletRequest httpRequest) {
        DeviceInfoRequest deviceInfo = deviceInfoFactory.extractDeviceInfo(httpRequest);
        AuthResponse authResponse = authService.verifyRegistration(request, deviceInfo);
        MyApiResponse<AuthResponse> response = MyApiResponse.success(
                "Email verified successfully. You can now login.", authResponse);
        return ResponseEntity.ok(response);
    }

    @Override
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

    @Override
    @PostMapping("/validate")
    public ResponseEntity<MyApiResponse<TokenValidationResponse>> validateToken(
            @RequestParam String token) {
        TokenValidationResponse validationResponse = authService.validateToken(token);
        MyApiResponse<TokenValidationResponse> response = MyApiResponse.success("Token validated", validationResponse);
        return ResponseEntity.ok(response);
    }

    @Override
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

    @Override
    @PostMapping("/logout")
    public ResponseEntity<MyApiResponse<Void>> logout(HttpServletRequest request) {
        String token = extractTokenFromHeader(request);
        if (token != null) {
            authService.logout(token);
        }
        MyApiResponse<Void> response = MyApiResponse.success("Logout successful");
        return ResponseEntity.ok(response);
    }

    @Override
    @PostMapping("/logout-all")
    public ResponseEntity<MyApiResponse<Void>> logoutAllDevices(
            @RequestParam Long userId) {
        authService.logoutAllDevices(userId);
        MyApiResponse<Void> response = MyApiResponse.success("Logged out from all devices successfully");
        return ResponseEntity.ok(response);
    }

    @Override
    @GetMapping("/devices")
    public ResponseEntity<MyApiResponse<List<DeviceSessionResponse>>> getUserDevices(
            @RequestParam Long userId,
            HttpServletRequest request) {
        String currentToken = extractTokenFromHeader(request);
        List<DeviceSessionResponse> devices = deviceService.getUserDeviceSessions(userId, currentToken);
        MyApiResponse<List<DeviceSessionResponse>> response = MyApiResponse.success(
                "Device sessions retrieved successfully", devices);
        return ResponseEntity.ok(response);
    }

    @Override
    @PostMapping("/logout-device")
    public ResponseEntity<MyApiResponse<Void>> logoutDevice(
            @Valid @RequestBody LogoutDeviceRequest request,
            @RequestParam Long userId) {
        deviceService.logoutDevice(request, userId);
        MyApiResponse<Void> response = MyApiResponse.success("Device logged out successfully");
        return ResponseEntity.ok(response);
    }

    @Override
    @PostMapping("/change-password")
    public ResponseEntity<MyApiResponse<ChangePasswordResponse>> changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            @RequestParam Long userId) {
        ChangePasswordResponse changePasswordResponse = passwordService.changePassword(request, userId);
        MyApiResponse<ChangePasswordResponse> response = MyApiResponse.success(
                "Password change processed", changePasswordResponse);
        return ResponseEntity.ok(response);
    }

    @Override
    @PostMapping("/forgot-password")
    public ResponseEntity<MyApiResponse<ForgotPasswordResponse>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {
        ForgotPasswordResponse forgotPasswordResponse = authService.forgotPassword(request);
        MyApiResponse<ForgotPasswordResponse> response = MyApiResponse.success(
                "Password reset OTP sent successfully", forgotPasswordResponse);
        return ResponseEntity.ok(response);
    }

    @Override
    @PostMapping("/reset-password")
    public ResponseEntity<MyApiResponse<ResetPasswordResponse>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {
        ResetPasswordResponse resetPasswordResponse = authService.resetPassword(request);
        MyApiResponse<ResetPasswordResponse> response = MyApiResponse.success(
                "Password reset successfully", resetPasswordResponse);
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