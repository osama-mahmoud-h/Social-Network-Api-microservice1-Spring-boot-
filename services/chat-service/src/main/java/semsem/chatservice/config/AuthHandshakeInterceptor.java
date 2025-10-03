package semsem.chatservice.config;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import semsem.chatservice.security.JwtUtil;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class AuthHandshakeInterceptor implements HandshakeInterceptor {

    private final JwtUtil jwtUtil;

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
            System.out.println("Extracted token: " + token);

//            if (token != null && jwtUtil.validateToken(token)) {
//                String username = jwtUtil.extractUsername(token);
//                String userId = jwtUtil.extractUserId(token);
//
//                // Store user info in WebSocket session attributes
//                attributes.put("username", username);
//                attributes.put("userId", userId);
//                attributes.put("token", token);
//
//                System.out.println("✅ WebSocket connection authorized for user: " + username);
//                return true; // ✅ allow connection
//            }


            System.out.println("❌ WebSocket connection rejected - invalid or missing token");
            return true; // temporarily allow connection for testing
        }

        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        return false; // ❌ reject connection
    }

    /**
     * Extract JWT token from multiple possible sources
     */
    private String extractToken(HttpServletRequest request) {
        // 1. Authorization header (most common)
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }

        // 3. Cookie (alternative approach)
        if (request.getCookies() != null) {
            for (var cookie : request.getCookies()) {
                if ("jwt".equals(cookie.getName())) {
                    System.out.println("cookie. "+ cookie.getValue());
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
    }
}

