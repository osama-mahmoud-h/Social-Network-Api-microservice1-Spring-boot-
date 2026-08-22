package com.app.server.service.notification;

import com.app.shared.events.IntegrationEvent;
import com.app.shared.events.NotificationEvent;

public interface KafkaEventProducer {

    void sendNotification(NotificationEvent notificationEvent);

    void sendEventDto(IntegrationEvent eventDto, String topic);
}