package com.app.server.service.notification;

import com.app.server.dto.notification.NotificationEvent;

public interface KafkaNotificationProducer {
    void sendNotification(NotificationEvent notificationEvent);
}
