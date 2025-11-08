package com.app.auth.service;

import com.app.auth.model.User;
import com.app.auth.enums.UserRole;
import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

public interface JwtService {
    String extractUsername(String token);

    Long extractUserId(String token);

    Set<UserRole> extractRoles(String token);

    Date extractExpiration(String token);

    <T> T extractClaim(String token, Function<Claims, T> claimsResolver);

    String generateAccessToken(User user);

    String generateRefreshToken(User user);

    boolean isTokenValid(String token, User user);

    boolean isTokenExpired(String token);

    Instant getExpirationInstant(String token);

    /**
     * Validates token for filter - validates JWT signature, expiry, and revocation status
     * Checks database to ensure token is not revoked (logout support)
     * @param token JWT token to validate
     * @return Optional containing UserDetails if token is valid and not revoked, empty otherwise
     */
    Optional<UserDetails> validateTokenForFilter(String token);

    /**
     * Check if token is revoked in the database
     * @param token JWT token to check
     * @return true if token is revoked or doesn't exist, false otherwise
     */
    boolean isTokenRevoked(String token);
}
