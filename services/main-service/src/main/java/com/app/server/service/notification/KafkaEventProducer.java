package com.app.server.service.notification;

import com.app.server.dto.notification.NotificationEvent;
import com.app.server.enums.KafkaTopics;

import java.io.Serializable;

public interface KafkaEventProducer {

    void sendNotification(NotificationEvent notificationEvent);

    void sendEventDto(Serializable eventDto, KafkaTopics topic);
}