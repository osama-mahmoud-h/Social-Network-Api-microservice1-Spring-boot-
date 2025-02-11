package semsem.notificationservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import semsem.notificationservice.dto.NotificationEvent;
import semsem.notificationservice.enums.NotificationType;
import semsem.notificationservice.factory.NotificationHandlerFactory;
import semsem.notificationservice.service.KafkaConsumer;

@Service
@RequiredArgsConstructor
public class KafkaConsumerImpl implements KafkaConsumer {
    private final NotificationHandlerFactory handlerFactory;
    private static final Logger log = LoggerFactory.getLogger(KafkaConsumerImpl.class);

    @KafkaListener(topics = "notification-events", groupId = "notification-group", containerFactory = "kafkaListenerContainerFactory")
    public void listen(ConsumerRecord<String, NotificationEvent> record) {

        log.info("Notification event received: {}", record.value());

        NotificationType type = record.value().getType();
        handlerFactory.getHandler(type).handle(record.value());

        log.info("Notification event processed: {}", type);
    }
}
