package com.app.server.service.notification;

import com.app.server.dto.notification.NotificationEvent;
import com.app.server.enums.KafkaTopics;

import java.io.Serializable;

/**
 * Service interface for sending notifications
 */
public interface NotificationService {
    /**
     * Send a notification event to notification-events topic
     */
    void sendNotification(NotificationEvent notificationEvent);

    /**
     * Send an event DTO to a specific topic (type-safe with Serializable constraint)
     */
    void sendEventDto(Serializable eventDto, KafkaTopics topic);
}
