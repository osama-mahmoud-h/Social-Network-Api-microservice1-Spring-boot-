package com.app.server.service.notification;

import com.app.server.dto.notification.NotificationEvent;
import com.app.server.enums.KafkaTopics;

import java.io.Serializable;

/**
 * Service for publishing events to Kafka topics
 * Provides type-safe methods for different event types
 */
public interface KafkaNotificationProducer {
    /**
     * Send a notification event to notification-events topic
     */
    void sendNotification(NotificationEvent notificationEvent);

    /**
     * Send an event DTO to a specific topic (type-safe with Serializable constraint)
     * @param eventDto The event DTO to send (PostEventDto, CommentEventDto, etc.)
     * @param topic The Kafka topic to publish to
     */
    void sendEventDto(Serializable eventDto, KafkaTopics topic);
}
