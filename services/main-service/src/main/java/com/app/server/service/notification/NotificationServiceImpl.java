package com.app.server.service.notification;

import com.app.server.dto.notification.NotificationEvent;
import com.app.server.enums.KafkaTopics;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final KafkaNotificationProducer kafkaProducerService;

    @Override
    public void sendNotification(NotificationEvent notificationEvent) {
        kafkaProducerService.sendNotification(notificationEvent);
    }

    @Override
    public void sendNotification(Object event, KafkaTopics topic) {
        kafkaProducerService.sendNotification(event, topic);
    }
}
