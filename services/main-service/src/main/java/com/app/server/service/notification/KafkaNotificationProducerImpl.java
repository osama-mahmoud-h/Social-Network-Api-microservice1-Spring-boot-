package com.app.server.service.notification;

import com.app.server.dto.notification.NotificationEvent;
import com.app.server.enums.KafkaTopics;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.io.Serializable;

@Service
@RequiredArgsConstructor
public class KafkaNotificationProducerImpl implements KafkaNotificationProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void sendNotification(NotificationEvent notificationEvent) {
        kafkaTemplate.send(KafkaTopics.NOTIFICATION_EVENTS.getValue(), notificationEvent);
        System.out.println("Notification sent: " + notificationEvent);
    }

    @Override
    public void sendNotification(Object event, KafkaTopics topic) {
        kafkaTemplate.send(topic.getValue(), event);
        System.out.println("general Notification sent: topic= "+topic +" , event= "+ event);
    }
}
