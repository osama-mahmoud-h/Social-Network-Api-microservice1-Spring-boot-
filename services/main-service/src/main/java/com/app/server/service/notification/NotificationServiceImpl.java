package com.app.server.service.notification;

import com.app.server.dto.notification.NotificationEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final KafkaNotificationProducer kafkaProducerService;

    @Override
    public void sendNotification(NotificationEvent notificationEvent) {
        kafkaProducerService.sendNotification(notificationEvent);
    }
}
