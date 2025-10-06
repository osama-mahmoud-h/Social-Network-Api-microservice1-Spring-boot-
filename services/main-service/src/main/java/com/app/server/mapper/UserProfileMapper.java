package com.app.server.mapper;

import com.app.server.event.UserCreatedEvent;
import com.app.server.model.UserProfile;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class UserProfileMapper {
    public UserProfile mapCreatedEventToUserProfile(UserCreatedEvent event) {
        return UserProfile.builder()
                .userId(event.getUserId())
                .firstName(event.getFirstName())
                .lastName(event.getLastName())
                .email(event.getEmail())
                .phoneNumber(event.getPhoneNumber())
                .createdAt(event.getCreatedAt())
                .syncedAt(Instant.now())
                .build();
    }

    public void updateUserProfileFromEvent(UserProfile userProfile, UserCreatedEvent event) {
        userProfile.setFirstName(event.getFirstName());
        userProfile.setLastName(event.getLastName());
        userProfile.setEmail(event.getEmail());
        userProfile.setPhoneNumber(event.getPhoneNumber());
        userProfile.setSyncedAt(Instant.now());
    }
}
