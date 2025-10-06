package com.app.server.consumer;

import com.app.server.event.UserCreatedEvent;
import com.app.server.mapper.UserProfileMapper;
import com.app.server.model.UserProfile;
import com.app.server.repository.UserProfileRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserEventConsumer {

    private final UserProfileRepository userProfileRepository;
    private final ObjectMapper objectMapper;
    private final UserProfileMapper userProfileMapper;

    @KafkaListener(topics = "user-events", groupId = "main-service-group")
    public void consumeUserEvent(String eventJson) {
        try {
            log.debug("Received event: {}", eventJson);

            // Handle null or empty messages
            if (eventJson == null || eventJson.trim().isEmpty()) {
                log.warn("Received null or empty event, skipping");
                return;
            }

            // Parse event to determine type
            UserCreatedEvent event = objectMapper.readValue(eventJson, UserCreatedEvent.class);

            if ("USER_CREATED".equals(event.getEventType())) {
                handleUserCreated(event);
            } else if ("USER_UPDATED".equals(event.getEventType())) {
                handleUserUpdated(event);
            } else {
                log.warn("Unknown event type: {}", event.getEventType());
            }

        } catch (Exception e) {
            log.error("Error processing user event: {}, raw message: {}", e.getMessage(), eventJson, e);
            // Don't rethrow - let the message be committed and move on
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
        UserProfile userProfile = userProfileMapper.mapCreatedEventToUserProfile(event);

        userProfileRepository.save(userProfile);
        log.info("Created UserProfile for userId: {}", event.getUserId());
    }

    private void handleUserUpdated(UserCreatedEvent event) {
        log.info("Processing UserUpdatedEvent for userId: {}", event.getUserId());

        UserProfile userProfile = userProfileRepository.findById(event.getUserId())
                .orElseThrow(() -> new RuntimeException("UserProfile not found for userId: " + event.getUserId()));

        // Update fields using mapper
        userProfileMapper.updateUserProfileFromEvent(userProfile, event);

        userProfileRepository.save(userProfile);
        log.info("Updated UserProfile for userId: {}", event.getUserId());
    }
}