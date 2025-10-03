package com.app.shared.security.utils;

import com.app.shared.security.dto.TokenValidationResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Set;

public class SecurityUtils {

    public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getDetails() instanceof TokenValidationResponse) {
            TokenValidationResponse details = (TokenValidationResponse) authentication.getDetails();
            return details.getUserId();
        }
        return null;
    }

    public static String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            return authentication.getName();
        }
        return null;
    }

    public static Set<String> getCurrentUserRoles() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getDetails() instanceof TokenValidationResponse) {
            TokenValidationResponse details = (TokenValidationResponse) authentication.getDetails();
            return details.getRoles();
        }
        return Set.of();
    }

    public static boolean hasRole(String role) {
        return getCurrentUserRoles().contains(role);
    }

    public static boolean isAdmin() {
        return hasRole("ADMIN");
    }

    public static boolean isUser() {
        return hasRole("USER");
    }

    public static boolean isModerator() {
        return hasRole("MODERATOR");
    }

    public static boolean isCurrentUser(Long userId) {
        Long currentUserId = getCurrentUserId();
        return currentUserId != null && currentUserId.equals(userId);
    }

    public static boolean canAccessUserData(Long userId) {
        return isAdmin() || isCurrentUser(userId);
    }
}
