package com.app.auth.service.impl;

import com.app.auth.dto.request.DeviceInfoRequest;
import com.app.auth.dto.request.ForgotPasswordRequest;
import com.app.auth.dto.request.RegisterRequest;
import com.app.auth.dto.request.ResetPasswordRequest;
import com.app.auth.dto.request.SendOtpRequest;
import com.app.auth.dto.request.VerifyOtpRequest;
import com.app.auth.dto.request.VerifyRegistrationRequest;
import com.app.auth.dto.response.AuthResponse;
import com.app.auth.dto.response.ForgotPasswordResponse;
import com.app.auth.dto.response.OtpResponse;
import com.app.auth.dto.response.RegistrationResponse;
import com.app.auth.dto.response.ResetPasswordResponse;
import com.app.auth.dto.response.TokenValidationResponse;
import com.app.auth.enums.OtpStatus;
import com.app.auth.enums.OtpType;
import com.app.auth.enums.UserRole;
import com.app.auth.event.UserCreatedEvent;
import com.app.auth.exception.*;
import com.app.auth.mapper.AuthMapper;
import com.app.auth.mapper.DeviceMapper;
import com.app.auth.model.Token;
import com.app.auth.model.User;
import com.app.auth.publisher.UserEventPublisher;
import com.app.auth.repository.TokenRepository;
import com.app.auth.repository.UserRepository;
import com.app.auth.service.AuthService;
import com.app.auth.service.EmailService;
import com.app.auth.service.JwtService;
import com.app.auth.service.OtpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final JwtService jwtService;
    private final AuthMapper authMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserEventPublisher userEventPublisher;
    private final DeviceMapper deviceMapper;
    private final EmailService emailService;
    private final OtpService otpService;

    @Transactional
    @Override
    public AuthResponse authenticate(Authentication authentication, DeviceInfoRequest deviceInfo) {
        User user = (User) authentication.getPrincipal();

        // Update last login time
        user.setLastLoginAt(Instant.now());
        userRepository.save(user);

        // Generate new tokens
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        // Save the new token with device information
        saveUserToken(user, accessToken, deviceInfo);

        return authMapper.mapToAuthResponse(accessToken, refreshToken, 3600L, user);
    }

    @Override
    public TokenValidationResponse validateToken(String token) {
        try {
            if (jwtService.isTokenExpired(token)) {
                return TokenValidationResponse.invalid("Token is expired");
            }

            // Check if token is revoked
            Optional<Token> storedToken = tokenRepository.findByToken(token);
            System.out.println("Stored Token: " + storedToken);
            if (storedToken.isEmpty() || ( storedToken.get().isExpired() || storedToken.get().isRevoked()) ) {
                throw new TokenExpiredException("Token is revoked or expired");
            }

            String email = jwtService.extractUsername(token);
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));

            if (!jwtService.isTokenValid(token, user)) {
                throw new InvalidTokenException("Token is invalid for user: " + email);
            }

            // Convert UserRole enum to String
            Set<UserRole> roleNames = user.getRoles();

            return TokenValidationResponse.valid(user.getUserId(), user.getEmail(), roleNames);

        } catch (Exception e) {
            log.error("Token validation failed: {}", e.getMessage());
            throw new RuntimeException("Token validation failed: " + e.getMessage());
        }
    }

    @Transactional
    @Override
    public void logout(String token) {
        Optional<Token> storedToken = tokenRepository.findByToken(token);
        storedToken.ifPresentOrElse(tokenRepository::delete, () -> {
            log.warn("Attempted to logout with a token that does not exist: {}", token);
        });
    }

    @Transactional
    @Override
    public void logoutAllDevices(Long userId) {
        List<Token> validTokens = tokenRepository.findAllValidTokenByUser(userId);
        tokenRepository.deleteAll(validTokens);
    }

    @Transactional
    @Override
    public RegistrationResponse register(RegisterRequest request) {
        // Check if user already exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("User with email " + request.getEmail() + " already exists");
        }

        // Create new user using mapper - user is disabled and unverified until OTP verification
        User user = authMapper.mapToUser(request, passwordEncoder.encode(request.getPassword()));
        user.setEnabled(false);  // User cannot login until email is verified
        user.setEmailVerified(false);
        userRepository.save(user);

        // Generate and send OTP
        SendOtpRequest otpRequest = SendOtpRequest.builder()
                .email(request.getEmail())
                .type(OtpType.REGISTRATION)
                .build();

        OtpResponse otpResponse = otpService.sendOtp(otpRequest);

        log.info("User registered successfully. OTP sent to email: {}", request.getEmail());

        return RegistrationResponse.builder()
                .message("Registration successful. Please verify your email with the OTP sent to " + request.getEmail())
                .email(request.getEmail())
                .otpExpiresAt(otpResponse.getExpiresAt())
                .build();
    }

    @Transactional
    @Override
    public AuthResponse verifyRegistration(VerifyRegistrationRequest request, DeviceInfoRequest deviceInfo) {
        // Verify the OTP first
        VerifyOtpRequest verifyOtpRequest = VerifyOtpRequest.builder()
                .email(request.getEmail())
                .code(request.getCode())
                .type(OtpType.REGISTRATION)
                .build();

        OtpResponse otpResponse = otpService.verifyOtp(verifyOtpRequest);

        // Check if OTP verification was successful
        if (otpResponse.getStatus() != OtpStatus.VERIFIED) {
            throw new UnauthorizedAccessException(otpResponse.getMessage());
        }

        // Find the user and enable their account
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + request.getEmail()));

        user.setEnabled(true);
        user.setEmailVerified(true);
        user.setLastLoginAt(Instant.now());
        userRepository.save(user);

        // Generate tokens
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        // Save the token with device information
        saveUserToken(user, accessToken, deviceInfo);

        // Publish UserCreatedEvent to Kafka for main-service
        UserCreatedEvent event = authMapper.mapToUserCreatedEvent(user);
        userEventPublisher.publishUserCreated(event);

        log.info("User registration verified and completed for email: {}", request.getEmail());

        return authMapper.mapToAuthResponse(accessToken, refreshToken, 3600L, user);
    }

    @Transactional
    @Override
    public ForgotPasswordResponse forgotPassword(ForgotPasswordRequest request) {
        // Verify that user exists
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + request.getEmail()));

        // Generate and send OTP for password reset
        SendOtpRequest otpRequest = SendOtpRequest.builder()
                .email(request.getEmail())
                .type(OtpType.PASSWORD_RESET)
                .build();

        OtpResponse otpResponse = otpService.sendOtp(otpRequest);

        log.info("Password reset OTP sent to email: {}", request.getEmail());

        return ForgotPasswordResponse.builder()
                .message("Password reset OTP has been sent to " + request.getEmail())
                .email(request.getEmail())
                .otpExpiresAt(otpResponse.getExpiresAt())
                .build();
    }

    @Transactional
    @Override
    public ResetPasswordResponse resetPassword(ResetPasswordRequest request) {
        // Validate password confirmation
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("New password and confirm password do not match");
        }

        // Verify the OTP first
        VerifyOtpRequest verifyOtpRequest = VerifyOtpRequest.builder()
                .email(request.getEmail())
                .code(request.getCode())
                .type(OtpType.PASSWORD_RESET)
                .build();

        OtpResponse otpResponse = otpService.verifyOtp(verifyOtpRequest);

        // Check if OTP verification was successful
        if (otpResponse.getStatus() != OtpStatus.VERIFIED) {
            throw new RuntimeException(otpResponse.getMessage());
        }

        // Find the user and update password
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + request.getEmail()));

        // Update password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        // Logout from all devices for security
        logoutAllDevices(user.getUserId());

        log.info("Password reset successful for email: {}", request.getEmail());

        return ResetPasswordResponse.builder()
                .message("Password has been reset successfully. All active sessions have been logged out.")
                .build();
    }

    private void saveUserToken(User user, String jwtToken, DeviceInfoRequest deviceInfo) {
        Token.TokenBuilder tokenBuilder = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(Token.TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .expiresAt(jwtService.getExpirationInstant(jwtToken));

        // Enrich token with device information using mapper
        tokenBuilder = deviceMapper.enrichTokenWithDeviceInfo(tokenBuilder, deviceInfo);

        tokenRepository.save(tokenBuilder.build());
    }
}
