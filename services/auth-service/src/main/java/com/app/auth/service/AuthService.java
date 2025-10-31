package com.app.auth.service;

import com.app.auth.dto.AuthResponse;
import com.app.auth.dto.RegisterRequest;
import com.app.auth.dto.TokenValidationResponse;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

public interface AuthService {


    @Transactional
    AuthResponse authenticate(Authentication authentication);

    TokenValidationResponse validateToken(String token);

    @Transactional
    void logout(String token);

    @Transactional
    void logoutAllDevices(Long userId);

    @Transactional
    AuthResponse register(RegisterRequest request);
}
