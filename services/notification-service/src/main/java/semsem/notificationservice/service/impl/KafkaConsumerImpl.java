package semsem.notificationservice.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import semsem.notificationservice.dto.CommentEventDto;
import semsem.notificationservice.dto.NotificationEvent;
import semsem.notificationservice.dto.PostEventDto;
import semsem.notificationservice.enums.NotificationType;
import semsem.notificationservice.factory.NotificationHandlerFactory;
import semsem.notificationservice.handler.CommentEventHandler;
import semsem.notificationservice.handler.PostEventHandler;
import semsem.notificationservice.service.KafkaConsumer;

/**
 * Kafka consumer service for handling various domain events
 * Delegates event processing to specialized handlers
 */
@Service
@RequiredArgsConstructor
public class KafkaConsumerImpl implements KafkaConsumer {
    private static final Logger log = LoggerFactory.getLogger(KafkaConsumerImpl.class);

    private final NotificationHandlerFactory notificationHandlerFactory;
    private final PostEventHandler postEventHandler;
    private final CommentEventHandler commentEventHandler;
    private final ObjectMapper objectMapper;

    /**
     * Listen to notification events and delegate to appropriate handlers
     */
    @KafkaListener(topics = "notification-events", groupId = "notification-group", containerFactory = "kafkaListenerContainerFactory")
    public void consumeNotificationEvent(ConsumerRecord<String, NotificationEvent> record) {
        try {
            log.info("Notification event received: type={}, senderId={}, receiverId={}",
                     record.value().getType(), record.value().getSenderId(), record.value().getReceiverId());

            NotificationType type = record.value().getType();
            notificationHandlerFactory.getHandler(type).handle(record.value());

            log.info("Notification event processed successfully: {}", type);
        } catch (Exception e) {
            log.error("Error processing notification event: {}", e.getMessage(), e);
            // TODO: Send to DLQ or implement retry logic
        }
    }

    /**
     * Listen to post events and delegate to PostEventHandler
     */
    @KafkaListener(topics = "post-events", groupId = "notification-group", containerFactory = "kafkaListenerContainerFactory")
    public void consumePostEvent(ConsumerRecord<String, Object> record) {
        try {
            log.info("Post event received from Kafka");

            // Convert Object to PostEventDto using ObjectMapper
            PostEventDto event = objectMapper.convertValue(record.value(), PostEventDto.class);

            // Delegate to specialized handler
            postEventHandler.handle(event);

            log.info("Post event processed successfully: action={}, postId={}",
                     event.getActionType(), event.getPostId());

        } catch (Exception e) {
            log.error("Error processing post event: {}", e.getMessage(), e);
            // TODO: Send to DLQ or implement retry logic
        }
    }

    /**
     * Listen to comment events and delegate to CommentEventHandler
     */
    @KafkaListener(topics = "comment-events", groupId = "notification-group", containerFactory = "kafkaListenerContainerFactory")
    public void consumeCommentEvent(ConsumerRecord<String, Object> record) {
        try {
            log.info("Comment event received from Kafka");

            // Convert Object to CommentEventDto using ObjectMapper
            CommentEventDto event = objectMapper.convertValue(record.value(), CommentEventDto.class);

            // Delegate to specialized handler
            commentEventHandler.handle(event);

            log.info("Comment event processed successfully: action={}, commentId={}",
                     event.getActionType(), event.getCommentId());

        } catch (Exception e) {
            log.error("Error processing comment event: {}", e.getMessage(), e);
            // TODO: Send to DLQ or implement retry logic
        }
    }
}
