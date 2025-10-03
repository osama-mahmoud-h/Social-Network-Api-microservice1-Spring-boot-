package com.app.server.consumer;

import com.app.server.event.UserCreatedEvent;
import com.app.server.model.UserProfile;
import com.app.server.repository.UserProfileRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserEventConsumer {

    private final UserProfileRepository userProfileRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "user-events", groupId = "main-service-group")
    public void consumeUserEvent(String eventJson) {
        try {
            // Parse event to determine type
            UserCreatedEvent event = objectMapper.readValue(eventJson, UserCreatedEvent.class);

            if ("USER_CREATED".equals(event.getEventType())) {
                handleUserCreated(event);
            } else if ("USER_UPDATED".equals(event.getEventType())) {
                handleUserUpdated(event);
            }

        } catch (Exception e) {
            log.error("Error processing user event: {}", e.getMessage(), e);
        }
    }

    private void handleUserCreated(UserCreatedEvent event) {
        log.info("Processing UserCreatedEvent for userId: {}", event.getUserId());

        // Check if user profile already exists
        if (userProfileRepository.findById(event.getUserId()).isPresent()) {
            log.warn("UserProfile already exists for userId: {}", event.getUserId());
            return;
        }

        // Create UserProfile
        UserProfile userProfile = UserProfile.builder()
                .userId(event.getUserId())
                .firstName(event.getFirstName())
                .lastName(event.getLastName())
                .email(event.getEmail())
                .phoneNumber(event.getPhoneNumber())
                .createdAt(event.getCreatedAt())
                .syncedAt(Instant.now())
                .build();

        userProfileRepository.save(userProfile);
        log.info("Created UserProfile for userId: {}", event.getUserId());
    }

    private void handleUserUpdated(UserCreatedEvent event) {
        log.info("Processing UserUpdatedEvent for userId: {}", event.getUserId());

        UserProfile userProfile = userProfileRepository.findById(event.getUserId())
                .orElseThrow(() -> new RuntimeException("UserProfile not found for userId: " + event.getUserId()));

        // Update fields
        userProfile.setFirstName(event.getFirstName());
        userProfile.setLastName(event.getLastName());
        userProfile.setEmail(event.getEmail());
        userProfile.setPhoneNumber(event.getPhoneNumber());
        userProfile.setSyncedAt(Instant.now());

        userProfileRepository.save(userProfile);
        log.info("Updated UserProfile for userId: {}", event.getUserId());
    }
}