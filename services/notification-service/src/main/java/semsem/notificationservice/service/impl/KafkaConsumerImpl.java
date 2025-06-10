package semsem.notificationservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import semsem.notificationservice.dto.NotificationEvent;
import semsem.notificationservice.enums.KafkaTopics;
import semsem.notificationservice.enums.NotificationType;
import semsem.notificationservice.factory.NotificationHandlerFactory;
import semsem.notificationservice.service.KafkaConsumer;

import static semsem.notificationservice.enums.KafkaTopics.POST_EVENTS;

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

    @KafkaListener(topics = "post-events", groupId = "notification-group", containerFactory = "kafkaListenerContainerFactory")
    public void listenPostEvents(ConsumerRecord<String, Object> record) {
        System.out.println("Post event received: " + record.value());
        log.info("Post event received: {}", record.value());
    }

    @KafkaListener(topics = "comment-events", groupId = "notification-group", containerFactory = "kafkaListenerContainerFactory")
    public void listenCommentEvents(ConsumerRecord<String, Object> record) {
        System.out.println("Comment event received: " + record.value());
        log.info("Comment event received: {}", record.value());
    }

}
