package com.app.auth.service.impl;

import com.app.auth.event.UserCreatedEvent;
import com.app.auth.event.UserUpdatedEvent;
import com.app.auth.model.OutboxEvent;
import com.app.auth.repository.OutboxEventRepository;
import com.app.auth.service.OutboxEventService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OutboxEventServiceImpl implements OutboxEventService {

    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    private static final String AGGREGATE_TYPE_USER = "User";

    @Override
    public void saveUserCreatedEvent(UserCreatedEvent event) {
        saveOutboxEvent(AGGREGATE_TYPE_USER, event.getUserId(), event.getEventType(), event);
    }

    @Override
    public void saveUserUpdatedEvent(UserUpdatedEvent event) {
        saveOutboxEvent(AGGREGATE_TYPE_USER, event.getUserId(), event.getEventType(), event);
    }

    private void saveOutboxEvent(String aggregateType, Long aggregateId, String eventType, Object event) {
        try {
            String payload = objectMapper.writeValueAsString(event);

            OutboxEvent outboxEvent = OutboxEvent.builder()
                    .aggregateType(aggregateType)
                    .aggregateId(aggregateId)
                    .eventType(eventType)
                    .payload(payload)
                    .build();

            outboxEventRepository.save(outboxEvent);
            log.info("Saved outbox event: type={}, aggregateId={}", eventType, aggregateId);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize event for outbox: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to serialize event for outbox", e);
        }
    }
}