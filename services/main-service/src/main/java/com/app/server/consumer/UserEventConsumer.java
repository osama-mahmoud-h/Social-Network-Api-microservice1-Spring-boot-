package com.app.server.consumer;

import com.app.server.event.app.cdc.UserCreatedEvent;
import com.app.server.mapper.UserProfileMapper;
import com.app.server.model.UserProfile;
import com.app.server.repository.UserProfileRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
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
    public void consumeUserEvent(String eventJson) throws JsonProcessingException {
        log.debug("Received user event: {}", eventJson);
        if (eventJson == null || eventJson.isBlank()) {
            log.warn("Received blank user event, skipping");
            return;
        }

        UserCreatedEvent event = objectMapper.readValue(eventJson, UserCreatedEvent.class);

        if ("true".equals(event.getDeleted())) {
            userProfileRepository.deleteById(event.getUserId());
            log.info("Deleted UserProfile userId={}", event.getUserId());
            return;
        }

        UserProfile existing = userProfileRepository.findById(event.getUserId()).orElse(null);
        UserProfile userProfile = userProfileMapper.toUserProfile(event, existing);
        userProfileRepository.save(userProfile);
        log.info("Upserted UserProfile userId={} op={}", event.getUserId(), event.getOp());
    }
}