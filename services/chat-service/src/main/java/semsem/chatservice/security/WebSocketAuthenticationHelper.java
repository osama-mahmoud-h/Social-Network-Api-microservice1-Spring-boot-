package semsem.chatservice.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Helper class to extract authenticated user information from WebSocket sessions.
 * User data is stored in session attributes during the handshake by AuthHandshakeInterceptor.
 */
@Slf4j
@Component
public class WebSocketAuthenticationHelper {

    /**
     * Extract the authenticated user ID from WebSocket session attributes
     *
     * @param headerAccessor The message header accessor
     * @return The user ID, or null if not found
     */
    public  Long getUserId(SimpMessageHeaderAccessor headerAccessor) {
        if (headerAccessor == null || headerAccessor.getSessionAttributes() == null) {
            log.warn("Cannot extract userId - headerAccessor or sessionAttributes is null");
            return null;
        }
        return (Long) headerAccessor.getSessionAttributes().get("userId");
    }

    /**
     * Extract the authenticated user email from WebSocket session attributes
     *
     * @param headerAccessor The message header accessor
     * @return The user email, or null if not found
     */
    public  String getEmail(SimpMessageHeaderAccessor headerAccessor) {
        if (headerAccessor == null || headerAccessor.getSessionAttributes() == null) {
            log.warn("Cannot extract email - headerAccessor or sessionAttributes is null");
            return null;
        }
        return (String) headerAccessor.getSessionAttributes().get("email");
    }

    /**
     * Extract the authenticated user roles from WebSocket session attributes
     *
     * @param headerAccessor The message header accessor
     * @return The user roles, or null if not found
     */
    @SuppressWarnings("unchecked")
    public  Set<String> getRoles(SimpMessageHeaderAccessor headerAccessor) {
        if (headerAccessor == null || headerAccessor.getSessionAttributes() == null) {
            log.warn("Cannot extract roles - headerAccessor or sessionAttributes is null");
            return null;
        }
        return (Set<String>) headerAccessor.getSessionAttributes().get("roles");
    }

    /**
     * Extract the JWT token from WebSocket session attributes
     *
     * @param headerAccessor The message header accessor
     * @return The JWT token, or null if not found
     */
    public  String getToken(SimpMessageHeaderAccessor headerAccessor) {
        if (headerAccessor == null || headerAccessor.getSessionAttributes() == null) {
            log.warn("Cannot extract token - headerAccessor or sessionAttributes is null");
            return null;
        }
        return (String) headerAccessor.getSessionAttributes().get("token");
    }

    /**
     * Check if the user has a specific role
     *
     * @param headerAccessor The message header accessor
     * @param role The role to check
     * @return true if the user has the role, false otherwise
     */
    public  boolean hasRole(SimpMessageHeaderAccessor headerAccessor, String role) {
        Set<String> roles = getRoles(headerAccessor);
        return roles != null && roles.contains(role);
    }

    /**
     * Verify that the sender ID matches the authenticated user ID
     * This prevents users from impersonating other users
     *
     * @param headerAccessor The message header accessor
     * @param claimedSenderId The sender ID claimed in the message
     * @return true if the sender ID matches the authenticated user, false otherwise
     */
    public  boolean verifySender(SimpMessageHeaderAccessor headerAccessor, String claimedSenderId) {
        Long authenticatedUserId = getUserId(headerAccessor);
        if (authenticatedUserId == null) {
            log.warn("Cannot verify sender - no authenticated user ID found");
            return false;
        }

        boolean isValid = authenticatedUserId.toString().equals(claimedSenderId);
        if (!isValid) {
            log.warn("Sender verification failed - authenticated user: {}, claimed sender: {}",
                    authenticatedUserId, claimedSenderId);
        }
        return isValid;
    }
}