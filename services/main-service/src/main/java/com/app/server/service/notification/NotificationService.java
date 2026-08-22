package com.app.server.service.notification;

import com.app.shared.events.IntegrationEvent;
import com.app.shared.events.NotificationEvent;

/**
 * Service interface for sending notifications
 */
public interface NotificationService {
    /**
     * Send a notification event to notification-events topic
     */
    void sendNotification(NotificationEvent notificationEvent);

    /**
     * Send an integration event to a specific topic (see {@link com.app.shared.events.KafkaTopics})
     */
    void sendEventDto(IntegrationEvent eventDto, String topic);
}
