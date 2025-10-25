package com.app.server.service.notification;

import com.app.server.dto.notification.NotificationEvent;
import com.app.server.enums.KafkaTopics;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.Serializable;

/**
 * Implementation of NotificationService
 * Delegates to KafkaNotificationProducer
 */
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final KafkaNotificationProducer kafkaProducerService;

    @Override
    public void sendNotification(NotificationEvent notificationEvent) {
        kafkaProducerService.sendNotification(notificationEvent);
    }

    @Override
    public void sendEventDto(Serializable eventDto, KafkaTopics topic) {
        kafkaProducerService.sendEventDto(eventDto, topic);
    }
}
