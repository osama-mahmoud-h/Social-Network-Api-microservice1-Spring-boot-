package com.app.auth.service.impl;

import com.app.auth.dto.request.ChangePasswordRequest;
import com.app.auth.dto.response.ChangePasswordResponse;
import com.app.auth.enums.PasswordChangeResult;
import com.app.auth.exception.UserNotFoundException;
import com.app.auth.model.User;
import com.app.auth.repository.UserRepository;
import com.app.auth.service.AuthService;
import com.app.auth.service.PasswordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordServiceImpl implements PasswordService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;

    @Override
    @Transactional
    public ChangePasswordResponse changePassword(ChangePasswordRequest request, Long userId) {
        // Fetch user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        // Validate old password
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            log.warn("Invalid old password for user: {}", userId);
            return ChangePasswordResponse.failed(
                    PasswordChangeResult.INVALID_OLD_PASSWORD,
                    "Old password is incorrect"
            );
        }

        // Check if new passwords match
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            log.warn("New passwords do not match for user: {}", userId);
            return ChangePasswordResponse.failed(
                    PasswordChangeResult.PASSWORDS_DO_NOT_MATCH,
                    "New password and confirm password do not match"
            );
        }

        // Check if new password is same as old password
        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            log.warn("New password is same as old password for user: {}", userId);
            return ChangePasswordResponse.failed(
                    PasswordChangeResult.SAME_AS_OLD_PASSWORD,
                    "New password must be different from old password"
            );
        }

        // Validate password strength (can be enhanced with more complex rules)
        if (!isPasswordStrong(request.getNewPassword())) {
            log.warn("Weak password provided for user: {}", userId);
            return ChangePasswordResponse.failed(
                    PasswordChangeResult.WEAK_PASSWORD,
                    "Password is too weak. Must be at least 6 characters long"
            );
        }

        // Update password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        // Invalidate all existing tokens (logout from all devices for security)
        authService.logoutAllDevices(userId);

        log.info("Password changed successfully for user: {}", userId);
        return ChangePasswordResponse.success();
    }

    private boolean isPasswordStrong(String password) {
        // Basic validation - can be enhanced with more rules
        return password != null && password.length() >= 6;
    }
}
