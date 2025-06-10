package com.app.server.service.notification;

import com.app.server.dto.notification.NotificationEvent;
import com.app.server.enums.KafkaTopics;

public interface KafkaNotificationProducer {
    void sendNotification(NotificationEvent notificationEvent);

    void sendNotification(Object event, KafkaTopics topic);
}
