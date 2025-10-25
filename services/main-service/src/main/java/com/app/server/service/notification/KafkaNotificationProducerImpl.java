package com.app.server.service.notification;

import com.app.server.dto.notification.NotificationEvent;
import com.app.server.enums.KafkaTopics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.io.Serializable;

/**
 * Implementation of Kafka notification producer
 * Publishes events to appropriate Kafka topics
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaNotificationProducerImpl implements KafkaNotificationProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void sendNotification(NotificationEvent notificationEvent) {
        try {
            kafkaTemplate.send(KafkaTopics.NOTIFICATION_EVENTS.getValue(), notificationEvent);
            log.info("Notification event sent: type={}, senderId={}, receiverId={}",
                     notificationEvent.getType(), notificationEvent.getSenderId(), notificationEvent.getReceiverId());
        } catch (Exception e) {
            log.error("Failed to send notification event: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void sendEventDto(Serializable eventDto, KafkaTopics topic) {
        try {
            kafkaTemplate.send(topic.getValue().toLowerCase(), eventDto);
            log.info("Event DTO sent to topic '{}': eventDto={}",
                     topic.getValue(), eventDto.getClass().getSimpleName());
        } catch (Exception e) {
            log.error("Failed to send event DTO to topic '{}': {}", topic.getValue(), e.getMessage(), e);
            throw e;
        }
    }
}
