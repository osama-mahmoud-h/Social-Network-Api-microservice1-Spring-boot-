package com.app.shared.security.filter;

import com.app.shared.security.client.AuthServiceClient;
import com.app.shared.security.dto.MyApiResponse;
import com.app.shared.security.dto.TokenValidationResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String MDC_USER_ID = "userId";

    private final AuthServiceClient authServiceClient;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                  HttpServletResponse response,
                                  FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        boolean userIdPushed = false;
        try {
            MyApiResponse<TokenValidationResponse> apiResponse = authServiceClient.validateToken(authHeader);

            if (!apiResponse.isSuccess() || apiResponse.getData() == null) {
                log.warn("Token validation failed: {}", apiResponse.getMessage());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\":\"" + apiResponse.getMessage() + "\"}");
                return;
            }

            TokenValidationResponse validationResponse = apiResponse.getData();
            log.debug("Validation Response: {}", validationResponse);

            if (!validationResponse.isValid()) {
                log.warn("Token validation failed: {}", validationResponse.getMessage());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\":\"" + validationResponse.getMessage() + "\"}");
                return;
            }

            if (validationResponse.getUserId() != null) {
                MDC.put(MDC_USER_ID, String.valueOf(validationResponse.getUserId()));
                userIdPushed = true;
            }

            List<SimpleGrantedAuthority> authorities = validationResponse.getRoles().stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                    .collect(Collectors.toList());

            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    validationResponse.getEmail(),
                    null,
                    authorities
            );
            authToken.setDetails(validationResponse);
            SecurityContextHolder.getContext().setAuthentication(authToken);

        } catch (Exception e) {
            log.error("Error validating token", e);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Authentication failed\"}");
            return;
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            if (userIdPushed) {
                MDC.remove(MDC_USER_ID);
            }
        }
    }
}