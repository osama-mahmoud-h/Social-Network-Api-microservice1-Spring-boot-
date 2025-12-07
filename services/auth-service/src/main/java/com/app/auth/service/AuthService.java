package com.app.auth.service;

import com.app.auth.dto.request.DeviceInfoRequest;
import com.app.auth.dto.request.ForgotPasswordRequest;
import com.app.auth.dto.request.RegisterRequest;
import com.app.auth.dto.request.ResetPasswordRequest;
import com.app.auth.dto.request.VerifyRegistrationRequest;
import com.app.auth.dto.response.AuthResponse;
import com.app.auth.dto.response.ForgotPasswordResponse;
import com.app.auth.dto.response.RegistrationResponse;
import com.app.auth.dto.response.ResetPasswordResponse;
import com.app.auth.dto.response.TokenValidationResponse;
import com.app.auth.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

public interface AuthService {


    @Transactional
    AuthResponse authenticate(Authentication authentication, DeviceInfoRequest deviceInfo);

    @Transactional
    AuthResponse authenticateOAuth2User(User user, DeviceInfoRequest deviceInfo);

    TokenValidationResponse validateToken(String token);

    @Transactional
    void logout(String token);

    @Transactional
    void logoutAllDevices(Long userId);

    @Transactional
    RegistrationResponse register(RegisterRequest request);

    @Transactional
    AuthResponse verifyRegistration(VerifyRegistrationRequest request, DeviceInfoRequest deviceInfo);

    @Transactional
    ForgotPasswordResponse forgotPassword(ForgotPasswordRequest request);

    @Transactional
    ResetPasswordResponse resetPassword(ResetPasswordRequest request);
}
