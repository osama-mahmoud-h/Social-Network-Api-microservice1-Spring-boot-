package com.app.auth.mapper;

import com.app.auth.dto.AuthResponse;
import com.app.auth.dto.RegisterRequest;
import com.app.auth.enums.UserEventType;
import com.app.auth.event.UserCreatedEvent;
import com.app.auth.model.User;
import com.app.auth.enums.UserRole;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Set;

@Component
public class AuthMapper {

    public AuthResponse.UserInfo mapToUserInfo(User user) {
        return AuthResponse.UserInfo.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .roles(user.getRoles())
                .build();
    }

    public AuthResponse mapToAuthResponse(String accessToken, String refreshToken, Long expiresIn, User user) {
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(expiresIn)
                .user(mapToUserInfo(user))
                .build();
    }

    public User mapToUser(RegisterRequest request, String encodedPassword) {
        return User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(encodedPassword)
                .phoneNumber(request.getPhoneNumber())
                .roles(Set.of(UserRole.USER))
                .enabled(true)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .createdAt(Instant.now())
                .build();
    }

    public UserCreatedEvent mapToUserCreatedEvent(User user) {
        return UserCreatedEvent.builder()
                .userId(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .createdAt(user.getCreatedAt())
                .eventType(UserEventType.USER_CREATED.name())
                .build();
    }
}