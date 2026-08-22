package com.app.server.service.notification;

import com.app.shared.events.IntegrationEvent;
import com.app.shared.events.KafkaTopics;
import com.app.shared.events.NotificationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaEventProducerImpl implements KafkaEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void sendNotification(NotificationEvent notificationEvent) {
        try {
            kafkaTemplate.send(KafkaTopics.NOTIFICATION_EVENTS, notificationEvent);
            log.info("Notification event sent: type={}, senderId={}, receiverId={}",
                     notificationEvent.getType(), notificationEvent.getSenderId(), notificationEvent.getReceiverId());
        } catch (Exception e) {
            log.error("Failed to send notification event: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void sendEventDto(IntegrationEvent eventDto, String topic) {
        try {
            kafkaTemplate.send(topic, eventDto);
            log.info("Event DTO sent to topic '{}': {}", topic, eventDto.getClass().getSimpleName());
        } catch (Exception e) {
            log.error("Failed to send event DTO to topic '{}': {}", topic, e.getMessage(), e);
            throw e;
        }
    }
}