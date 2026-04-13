package com.app.server.mapper;

import com.app.server.event.app.cdc.UserCreatedEvent;
import com.app.server.model.UserProfile;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class UserProfileMapper {

    public UserProfile toUserProfile(UserCreatedEvent event, UserProfile existing) {
        if (existing != null) {
            existing.setFirstName(event.getFirstName());
            existing.setLastName(event.getLastName());
            existing.setEmail(event.getEmail());
            existing.setPhoneNumber(event.getPhoneNumber());
            existing.setSyncedAt(Instant.now());
            return existing;
        }
        return UserProfile.builder()
                .userId(event.getUserId())
                .firstName(event.getFirstName())
                .lastName(event.getLastName())
                .email(event.getEmail())
                .phoneNumber(event.getPhoneNumber())
                .createdAt(Instant.parse(event.getCreatedAt()))
                .syncedAt(Instant.now())
                .build();
    }
}
