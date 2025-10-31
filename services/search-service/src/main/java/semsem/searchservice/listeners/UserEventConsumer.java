package semsem.searchservice.listeners;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import semsem.searchservice.mapper.AppUserIndexMapper;
import semsem.searchservice.model.AppUserIndex;
import semsem.searchservice.service.AppUserIndexService;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserEventConsumer {

    private final AppUserIndexService appUserIndexService;
    private final ObjectMapper objectMapper;
    private final AppUserIndexMapper appUserIndexMapper;

    @KafkaListener(topics = "user-events", groupId = "search-service-group", containerFactory = "kafkaListenerContainerFactory")
    public void consumeUserEvent(String eventJson) {
        try {
            log.debug("Received user event: {}", eventJson);

            // Handle null or empty messages
            if (eventJson == null || eventJson.trim().isEmpty()) {
                log.warn("Received null or empty user event, skipping");
                return;
            }

            // Parse event as a Map to extract event type
            Map<String, Object> event = objectMapper.readValue(eventJson, Map.class);
            String eventType = (String) event.get("eventType");

            if ("USER_CREATED".equals(eventType)) {
                handleUserCreated(event);
            } else if ("USER_UPDATED".equals(eventType)) {
                handleUserUpdated(event);
            } else {
                log.warn("Unknown user event type: {}", eventType);
            }

        } catch (Exception e) {
            log.error("Error processing user event: {}, raw message: {}", e.getMessage(), eventJson, e);
            // Don't rethrow - let the message be committed and move on
        }
    }

    private void handleUserCreated(Map<String, Object> event) {
        log.info("Processing UserCreatedEvent for userId: {}", event.get("userId"));

        try {
            AppUserIndex appUserIndex = appUserIndexMapper.AppUserEventObjectToAppUserIndex(event);
            String indexId = appUserIndexService.save(appUserIndex);
            log.info("Created AppUserIndex for userId: {} with indexId: {}", event.get("userId"), indexId);
        } catch (Exception e) {
            log.error("Error creating AppUserIndex: {}", e.getMessage(), e);
        }
    }

    private void handleUserUpdated(Map<String, Object> event) {
        log.info("Processing UserUpdatedEvent for userId: {}", event.get("userId"));

        try {
            AppUserIndex appUserIndex = appUserIndexMapper.AppUserEventObjectToAppUserIndex(event);
            // For updates, we need to set the ID if we have it stored
            // Since we don't have a way to query by userId yet, we'll just save it (upsert)
            appUserIndexService.save(appUserIndex);
            log.info("Updated AppUserIndex for userId: {}", event.get("userId"));
        } catch (Exception e) {
            log.error("Error updating AppUserIndex: {}", e.getMessage(), e);
        }
    }
}