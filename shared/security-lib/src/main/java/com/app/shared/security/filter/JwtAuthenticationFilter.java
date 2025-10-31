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

        // auth maybe jwt in cookies:


        try {
            MyApiResponse<TokenValidationResponse> apiResponse = authServiceClient.validateToken(authHeader);

            // Check if the API call was successful
            if (!apiResponse.isSuccess() || apiResponse.getData() == null) {
                log.warn("Token validation failed: {}", apiResponse.getMessage());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\":\"" + apiResponse.getMessage() + "\"}");
                return;
            }

            TokenValidationResponse validationResponse = apiResponse.getData();
            System.out.println("Validation Response: " + validationResponse);
            if (validationResponse.isValid()) {
                // Create authorities from roles
                List<SimpleGrantedAuthority> authorities = validationResponse.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                        .collect(Collectors.toList());

                // Create authentication token
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        validationResponse.getEmail(),
                        null,
                        authorities
                );

                // Add user details to the authentication
                authToken.setDetails(validationResponse);

                // Set authentication in security context
                SecurityContextHolder.getContext().setAuthentication(authToken);
            } else {
                System.out.println("Token validation failed: " + validationResponse.getMessage());
                log.warn("Token validation failed: {}", validationResponse.getMessage());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\":\"" + validationResponse.getMessage() + "\"}");
                return;
            }

        } catch (Exception e) {
            log.error("Error validating token: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Authentication failed\"}");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
