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

        // Map Post entity to simplified PostData DTO to avoid serialization issues
        PostEventDto.PostData postData = mapToPostData(event.getPost());

        PostEventDto postEventDto = PostEventDto.builder()
                .actionType(event.getActionType())
                .post(postData)
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

        // Map Comment entity to simplified CommentData DTO to avoid serialization issues
        CommentEventDto.CommentData commentData = mapToCommentData(event.getComment());

        CommentEventDto commentEventDto = CommentEventDto.builder()
                .actionType(event.getActionType())
                .comment(commentData)
                .commentId(event.getCommentId())
                .build();

        kafkaProducer.sendEventDto(commentEventDto, KafkaTopics.COMMENT_EVENTS);

        log.debug("Comment event published: commentId={}, action={}", event.getCommentId(), event.getActionType());
    }

    /**
     * Maps Post entity to PostData DTO for Kafka serialization
     * This avoids serialization issues with Hibernate proxies and lazy-loaded relationships
     */
    private PostEventDto.PostData mapToPostData(com.app.server.model.Post post) {
        // Map author data
        PostEventDto.AuthorData authorData = null;
        if (post.getAuthor() != null) {
            authorData = PostEventDto.AuthorData.builder()
                    .userId(post.getAuthor().getUserId())
                    .firstName(post.getAuthor().getFirstName())
                    .lastName(post.getAuthor().getLastName())
                    .build();
        }

        // Map post data
        return PostEventDto.PostData.builder()
                .postId(post.getPostId())
                .content(post.getContent())
                .createdAt(post.getCreatedAt() != null ? post.getCreatedAt().getEpochSecond() : null)
                .updatedAt(post.getUpdatedAt() != null ? post.getUpdatedAt().getEpochSecond() : null)
                .author(authorData)
                .build();
    }

    /**
     * Maps Comment entity to CommentData DTO for Kafka serialization
     * This avoids serialization issues with Hibernate proxies and lazy-loaded relationships
     */
    private CommentEventDto.CommentData mapToCommentData(com.app.server.model.Comment comment) {
        // Map author data
        CommentEventDto.AuthorData authorData = null;
        if (comment.getAuthor() != null) {
            authorData = CommentEventDto.AuthorData.builder()
                    .userId(comment.getAuthor().getUserId())
                    .firstName(comment.getAuthor().getFirstName())
                    .lastName(comment.getAuthor().getLastName())
                    .build();
        }

        // Extract post author ID for notifications
        Long postAuthorId = null;
        if (comment.getPost() != null && comment.getPost().getAuthor() != null) {
            postAuthorId = comment.getPost().getAuthor().getUserId();
        }

        // Map comment data
        return CommentEventDto.CommentData.builder()
                .commentId(comment.getCommentId())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt() != null ? comment.getCreatedAt().getEpochSecond() : null)
                .updatedAt(comment.getUpdatedAt() != null ? comment.getUpdatedAt().getEpochSecond() : null)
                .postId(comment.getPost() != null ? comment.getPost().getPostId() : null)
                .postAuthorId(postAuthorId)
                .parentCommentId(comment.getParentComment() != null ? comment.getParentComment().getCommentId() : null)
                .author(authorData)
                .build();
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
