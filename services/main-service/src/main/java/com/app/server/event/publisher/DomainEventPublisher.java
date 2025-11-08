package com.app.server.event.publisher;

import com.app.server.dto.notification.comment.CommentEventDto;
import com.app.server.dto.notification.NotificationEvent;
import com.app.server.dto.notification.post.PostEventDto;
import com.app.server.enums.KafkaTopics;
import com.app.server.event.domain.CommentDomainEvent;
import com.app.server.event.domain.FriendshipDomainEvent;
import com.app.server.event.domain.PostDomainEvent;
import com.app.server.service.notification.KafkaNotificationProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Listens to internal domain events and publishes them to Kafka
 * This decouples business logic from Kafka infrastructure
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DomainEventPublisher {

    private final KafkaNotificationProducer kafkaProducer;

    /**
     * Listens to PostDomainEvent and publishes to Kafka post-events topic
     */
    @Async
    @EventListener
    public void handlePostEvent(PostDomainEvent event) {
        log.info("Publishing post event to Kafka: {}", event.getEventType());

        PostEventDto postEventDto = PostEventDto.builder()
                .actionType(event.getActionType())
                .post(event.getPost())
                .postId(event.getPostId())
                .build();

        kafkaProducer.sendEventDto(postEventDto, KafkaTopics.POST_EVENTS);

        log.info("Post event published: postId={}, action={}", event.getPostId(), event.getActionType());
    }

    /**
     * Listens to CommentDomainEvent and publishes to Kafka comment-events topic
     */
    @Async
    @EventListener
    public void handleCommentEvent(CommentDomainEvent event) {
        log.info("Publishing comment event to Kafka: {}", event.getEventType());

        CommentEventDto commentEventDto = CommentEventDto.builder()
                .actionType(event.getActionType())
                .comment(event.getComment())
                .commentId(event.getCommentId())
                .build();

        kafkaProducer.sendEventDto(commentEventDto, KafkaTopics.COMMENT_EVENTS);

        log.debug("Comment event published: commentId={}, action={}", event.getCommentId(), event.getActionType());
    }

    /**
     * Listens to FriendshipDomainEvent and publishes to Kafka notification-events topic
     */
    @Async
    @EventListener
    public void handleFriendshipEvent(FriendshipDomainEvent event) {
        log.info("Publishing friendship event to Kafka: {}", event.getEventType());

        NotificationEvent notificationEvent = NotificationEvent.builder()
                .type(event.getNotificationType())
                .message(event.getMessage())
                .senderId(event.getSenderId())
                .receiverId(event.getReceiverId())
                .build();

        // sendNotification already targets NOTIFICATION_EVENTS topic
        kafkaProducer.sendNotification(notificationEvent);

        log.debug("Friendship notification published: type={}, senderId={}, receiverId={}",
                  event.getNotificationType(), event.getSenderId(), event.getReceiverId());
    }
}
