package com.app.server.service.impl;

import com.app.server.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
@Slf4j
@RequiredArgsConstructor
public class FileAccessValidator {

    private final FileRepository fileRepository;

    public boolean canAccessFileByObjectName(Long userId, String fileObjectName) {
        // TODO: Implement actual access logic
        return true;
    }

    public boolean canAccessFileById(Long userId, Long fileId) {
        if (userId == null || fileId == null) {
            log.warn("Invalid parameters: userId={}, fileId={}", userId, fileId);
            return false;
        }

        if (isAdmin()) {
            log.debug("Admin user {} granted access to file ID: {}", userId, fileId);
            return true;
        }

        try {
            Boolean hasAccess = fileRepository.canUserAccessFileById(fileId, userId);
            log.debug("User {} {} access to file ID: {}",
                    userId,
                    Boolean.TRUE.equals(hasAccess) ? "granted" : "denied",
                    fileId);
            return Boolean.TRUE.equals(hasAccess);
        } catch (Exception e) {
            log.error("Error checking file access for user {} and file ID {}: {}",
                    userId, fileId, e.getMessage(), e);
            return false;
        }
    }

    private boolean isAdmin() {
        try {
            Collection<? extends GrantedAuthority> authorities =
                    SecurityContextHolder.getContext().getAuthentication().getAuthorities();
            return authorities.stream()
                    .anyMatch(auth -> "ROLE_ADMIN".equals(auth.getAuthority()));
        } catch (Exception e) {
            log.debug("Error checking admin role: {}", e.getMessage());
            return false;
        }
    }
}
