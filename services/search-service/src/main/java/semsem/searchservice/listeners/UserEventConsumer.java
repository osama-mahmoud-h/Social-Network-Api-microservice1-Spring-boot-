package semsem.searchservice.listeners;

import com.fasterxml.jackson.core.JsonProcessingException;
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

    @KafkaListener(topics = "user-events", groupId = "search-service-group", containerFactory = "userEventListenerContainerFactory")
    public void consumeUserEvent(String eventJson) throws JsonProcessingException {
        if (eventJson == null || eventJson.isBlank()) {
            log.warn("Received blank user event, skipping");
            return;
        }

        Map<String, Object> event = objectMapper.readValue(eventJson, Map.class);

        if ("true".equals(event.get("__deleted"))) {
            Object userId = event.get("user_id");
            appUserIndexService.deleteByUserId(userId != null ? Long.valueOf(userId.toString()) : null);
            log.info("Deleted AppUserIndex userId={}", userId);
            return;
        }

        AppUserIndex appUserIndex = appUserIndexMapper.toAppUserIndex(event);
        appUserIndexService.upsert(appUserIndex);
        log.info("Upserted AppUserIndex userId={} op={}", event.get("user_id"), event.get("__op"));
    }
}