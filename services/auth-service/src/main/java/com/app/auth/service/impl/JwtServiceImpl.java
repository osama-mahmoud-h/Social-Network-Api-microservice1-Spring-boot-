package com.app.auth.service.impl;

import com.app.auth.enums.UserRole;
import com.app.auth.model.Token;
import com.app.auth.model.User;
import com.app.auth.repository.TokenRepository;
import com.app.auth.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtServiceImpl implements JwtService {

    private final TokenRepository tokenRepository;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpirationMs;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpirationMs;

    @Override
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    @Override
    public Long extractUserId(String token) {
        return extractClaim(token, claims -> claims.get("userId", Long.class));
    }

    @Override
    public Set<UserRole> extractRoles(String token) {
        return extractClaim(token, claims -> {
            @SuppressWarnings("unchecked")
            Set<String> roleStrings = (Set<String>) claims.get("roles");
            return roleStrings.stream()
                    .map(UserRole::valueOf)
                    .collect(Collectors.toSet());
        });
    }

    @Override
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    @Override
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    @Override
    public String generateAccessToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getUserId());
        claims.put("roles", user.getRoles().stream().map(Enum::name).collect(Collectors.toSet()));
        return generateToken(claims, user.getUsername(), jwtExpirationMs);
    }

    @Override
    public String generateRefreshToken(User user) {
        return generateToken(new HashMap<>(), user.getUsername(), refreshExpirationMs);
    }

    @Override
    public boolean isTokenValid(String token, User user) {
        final String username = extractUsername(token);
        return (username.equals(user.getUsername())) && !isTokenExpired(token);
    }

    @Override
    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    @Override
    public Instant getExpirationInstant(String token) {
        return extractExpiration(token).toInstant();
    }

    @Override
    public Optional<UserDetails> validateTokenForFilter(String token) {
        try {
            // 1. Validate JWT signature and expiry
            if (isTokenExpired(token)) {
                log.debug("Token is expired");
                return Optional.empty();
            }

            // 2. Check if token is revoked in database (supports logout)
            if (isTokenRevoked(token)) {
                log.debug("Token is revoked");
                return Optional.empty();
            }

            // 3. Extract user info from JWT claims
            String email = extractUsername(token);
            Set<UserRole> roles = extractRoles(token);

            // 4. Convert roles to Spring Security authorities
            List<SimpleGrantedAuthority> authorities = roles.stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                    .collect(Collectors.toList());

            // 5. Create Spring Security UserDetails
            UserDetails userDetails = org.springframework.security.core.userdetails.User
                    .withUsername(email)
                    .password("") // Password not needed for token-based auth
                    .authorities(authorities)
                    .accountExpired(false)
                    .accountLocked(false)
                    .credentialsExpired(false)
                    .disabled(false)
                    .build();

            return Optional.of(userDetails);

        } catch (Exception e) {
            log.error("Token validation failed: {}", e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public boolean isTokenRevoked(String token) {
        try {
            Optional<Token> storedToken = tokenRepository.findByToken(token);

            // Token doesn't exist in DB = revoked (deleted during logout)
            if (storedToken.isEmpty()) {
                return true;
            }

            // Check if token is marked as revoked or expired
            Token tokenEntity = storedToken.get();
            return tokenEntity.isRevoked() || tokenEntity.isExpired();

        } catch (Exception e) {
            log.error("Error checking token revocation: {}", e.getMessage());
            // On error, consider token revoked for security
            return true;
        }
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    private String generateToken(Map<String, Object> extraClaims, String subject, long expiration) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
}
