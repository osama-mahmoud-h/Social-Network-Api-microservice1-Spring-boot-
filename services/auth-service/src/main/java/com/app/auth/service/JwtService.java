package com.app.auth.service;

import com.app.auth.model.User;
import com.app.auth.enums.UserRole;
import io.jsonwebtoken.Claims;
import java.time.Instant;
import java.util.Date;
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
}
