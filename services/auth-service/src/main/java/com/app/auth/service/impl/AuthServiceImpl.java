package com.app.auth.service.impl;

import com.app.auth.dto.AuthResponse;
import com.app.auth.dto.RegisterRequest;
import com.app.auth.dto.TokenValidationResponse;
import com.app.auth.enums.UserRole;
import com.app.auth.event.UserCreatedEvent;
import com.app.auth.exception.InvalidTokenException;
import com.app.auth.exception.TokenExpiredException;
import com.app.auth.exception.UserAlreadyExistsException;
import com.app.auth.exception.UserNotFoundException;
import com.app.auth.mapper.AuthMapper;
import com.app.auth.model.Token;
import com.app.auth.model.User;
import com.app.auth.publisher.UserEventPublisher;
import com.app.auth.repository.TokenRepository;
import com.app.auth.repository.UserRepository;
import com.app.auth.service.AuthService;
import com.app.auth.service.JwtService;
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

    @Transactional
    @Override
    public AuthResponse authenticate(Authentication authentication) {
        User user = (User) authentication.getPrincipal();

        // Update last login time
        user.setLastLoginAt(Instant.now());
        userRepository.save(user);

        // Generate new tokens
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        // Save the new token
        saveUserToken(user, accessToken);

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
            if (storedToken.isPresent() && (storedToken.get().isExpired() || storedToken.get().isRevoked())) {
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
    public AuthResponse register(RegisterRequest request) {
        // Check if user already exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("User with email " + request.getEmail() + " already exists");
        }

        // Create new user using mapper
        User user = authMapper.mapToUser(request, passwordEncoder.encode(request.getPassword()));
        user = userRepository.save(user);

        // Generate tokens
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        // Save the token
        saveUserToken(user, accessToken);

        // Publish UserCreatedEvent to Kafka for main-service
        UserCreatedEvent event = authMapper.mapToUserCreatedEvent(user);
        userEventPublisher.publishUserCreated(event);

        return authMapper.mapToAuthResponse(accessToken, refreshToken, 3600L, user);
    }

    private void saveUserToken(User user, String jwtToken) {
        Token token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(Token.TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .expiresAt(jwtService.getExpirationInstant(jwtToken))
                .build();
        tokenRepository.save(token);
    }
}
