package semsem.chatservice.config;

import com.app.shared.security.client.AuthServiceClient;
import com.app.shared.security.dto.TokenValidationResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthHandshakeInterceptor implements HandshakeInterceptor {

    private final AuthServiceClient authServiceClient;

    @Override
    public boolean beforeHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes) throws Exception {

        if (request instanceof ServletServerHttpRequest servletRequest) {
            HttpServletRequest httpRequest = servletRequest.getServletRequest();

            // Try to get token from different sources
            String token = extractToken(httpRequest);
            log.debug("Extracted token for WebSocket handshake");
            System.out.println("Extracted token for WebSocket handshake: " + token);

            if (token != null) {
                try {
                    // Validate token via auth-service
                    TokenValidationResponse validationResponse =
                        authServiceClient.validateToken("Bearer " + token);

                    if (validationResponse.isValid()) {
                        // Store user info in WebSocket session attributes
                        attributes.put("userId", validationResponse.getUserId());
                        attributes.put("email", validationResponse.getEmail());
                        attributes.put("roles", validationResponse.getRoles());
                        attributes.put("token", token);

                        log.info("WebSocket connection authorized for user: {} (ID: {})",
                            validationResponse.getEmail(), validationResponse.getUserId());
                        return true; // allow connection
                    } else {
                        log.warn("WebSocket connection rejected - invalid token: {}",
                            validationResponse.getMessage());
                    }
                } catch (Exception e) {
                    log.error("Error validating token via auth-service: {}", e.getMessage());
                }
            } else {
                log.warn("WebSocket connection rejected - no token provided");
            }

            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return false; // reject connection
        }

        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        return false; // reject connection
    }

    /**
     * Extract JWT token from multiple possible sources
     */
    private String extractToken(HttpServletRequest request) {
        // 1. Authorization header (most common)
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            log.info("Token extracted from Authorization header");
            System.out.println("Token extracted from Authorization header");
            return authHeader.substring(7);
        }

        // 2. Cookie (alternative approach)
        if (request.getCookies() != null) {
            for (var cookie : request.getCookies()) {
                if ("jwt".equals(cookie.getName())) {
                    //log.info("Token extracted from cookie{cookie.getName()}"); fix this line in new line
                    System.out.println("Token extracted from cookie: " + cookie.getName());
                    log.debug("Token extracted from cookie");
                    return cookie.getValue();
                }
            }
        }

        return null;
    }

    @Override
    public void afterHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Exception ex) {
        // No action needed after handshake
    }
}

