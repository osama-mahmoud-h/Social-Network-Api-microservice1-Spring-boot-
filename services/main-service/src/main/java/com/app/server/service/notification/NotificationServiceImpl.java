package com.app.server.service.notification;

import com.app.shared.events.IntegrationEvent;
import com.app.shared.events.NotificationEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final KafkaEventProducer kafkaProducerService;

    @Override
    public void sendNotification(NotificationEvent notificationEvent) {
        kafkaProducerService.sendNotification(notificationEvent);
    }

    @Override
    public void sendEventDto(IntegrationEvent eventDto, String topic) {
        kafkaProducerService.sendEventDto(eventDto, topic);
    }
}
