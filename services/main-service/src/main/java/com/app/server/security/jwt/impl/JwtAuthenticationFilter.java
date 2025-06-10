package com.app.server.security.jwt.impl;


import com.app.server.model.AppUser;
import com.app.server.security.jwt.JwtService;
import com.app.server.service.AppUserService;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final AppUserService userService;
    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException ,RuntimeException {
        try {
            String jwt = getJwtFromRequest(request);
            if (jwt != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                validateAndAuthenticate(jwt, request);
            }
        } catch (Exception ex) {
            log.error("JWT Authentication failed: {}", ex.getMessage());
            String errorMessage = createCustomErrorMessage(ex, HttpServletResponse.SC_UNAUTHORIZED);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(errorMessage);

            return;
        }
        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.isNotEmpty(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private void validateAndAuthenticate(String jwt, HttpServletRequest request) {
        String userId = jwtService.extractUserName(jwt);
        String userEmail = jwtService.extractUserEmail(jwt);

        if (StringUtils.isNotEmpty(userId)) {
            AppUser appUser = (AppUser) userService.userDetailsService().loadUserByUsername(userEmail);
            if (jwtService.isTokenValid(jwt, appUser)) {
                authenticateUser(appUser, request);
            } else {
                log.error("Invalid JWT token for user: {}", userId);
                throw new SecurityException("Invalid JWT token.");
            }
        }
    }

    private void authenticateUser(AppUser appUser, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                appUser, null, appUser.getAuthorities());
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authenticationToken);
        SecurityContextHolder.setContext(context);
    }

    private String createCustomErrorMessage(Exception ex, int statusCode) {
        String timestamp = DateTimeFormatter.ISO_INSTANT.format(Instant.now().atOffset(ZoneOffset.UTC));
        return String.format("{\"timestamp\": \"%s\", \"status\": %d,\"message\": \"%s\"}",
                timestamp,
                statusCode,
                "Unauthorized"
        );
    }


}