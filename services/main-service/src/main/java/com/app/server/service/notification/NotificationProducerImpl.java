package com.app.server.service.notification;

import com.app.server.dto.notification.NotificationEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationProducerImpl implements KafkaNotificationProducer {
    private final KafkaTemplate<String, NotificationEvent> kafkaTemplate;

    @Override
    public void sendNotification(NotificationEvent notificationEvent) {
        kafkaTemplate.send("notification-events", notificationEvent);
    }
}
