package com.app.server.event.app.listener;

import com.app.shared.events.NotificationEvent;
import com.app.shared.events.CommentEventDto;
import com.app.shared.events.FriendshipEventDto;
import com.app.shared.events.PostEventDto;
import com.app.shared.events.ReactionEventDto;
import com.app.shared.events.KafkaTopics;
import com.app.server.event.app.domain.CommentDomainEvent;
import com.app.server.event.app.domain.FeedFriendshipDomainEvent;
import com.app.server.event.app.domain.FriendshipDomainEvent;
import com.app.server.event.app.domain.PostDomainEvent;
import com.app.server.event.app.domain.ReactionDomainEvent;
import com.app.server.mapper.CommentMapper;
import com.app.server.mapper.PostMapper;
import com.app.server.service.notification.KafkaEventProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class DomainEventListener {

    private final KafkaEventProducer kafkaProducer;
    private final PostMapper postMapper;
    private final CommentMapper commentMapper;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePostEvent(PostDomainEvent event) {
        log.info("Publishing post event to Kafka: {}", event.getEventType());

        PostEventDto postEventDto = postMapper.toPostEventDto(event.getPost(), event.getActionType());

        kafkaProducer.sendEventDto(postEventDto, KafkaTopics.POST_EVENTS);

        log.info("Post event published: postId={}, action={}", event.getPostId(), event.getActionType());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleCommentEvent(CommentDomainEvent event) {
        log.info("Publishing comment event to Kafka: {}", event.getEventType());

        CommentEventDto commentEventDto = commentMapper.toCommentEventDto(event.getComment(), event.getActionType());

        kafkaProducer.sendEventDto(commentEventDto, KafkaTopics.COMMENT_EVENTS);

        log.debug("Comment event published: commentId={}, action={}", event.getCommentId(), event.getActionType());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleReactionEvent(ReactionDomainEvent event) {
        log.info("Publishing reaction event to Kafka: {}", event.getEventType());

        ReactionEventDto reactionEventDto = ReactionEventDto.builder()
                .actionType(event.getActionType())
                // These two stay Strings on the wire: their enum forms are persisted in this
                // service's own tables, and pinning a DB-persisted enum to the published
                // contract would make every vocabulary change a migration for every consumer.
                .reactionType(event.getReactionType() != null ? event.getReactionType().name() : null)
                .targetType(event.getTargetType() != null ? event.getTargetType().name() : null)
                .targetId(event.getTargetId())
                .postId(event.getPostId())
                .reactorUserId(event.getReactorUserId())
                .build();

        kafkaProducer.sendEventDto(reactionEventDto, KafkaTopics.LIKE_EVENTS);

        log.debug("Reaction event published: postId={}, reactor={}, action={}",
                event.getPostId(), event.getReactorUserId(), event.getActionType());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleFeedFriendshipEvent(FeedFriendshipDomainEvent event) {
        log.info("Publishing feed friendship event to Kafka: {}", event.getEventType());

        FriendshipEventDto friendshipEventDto = FriendshipEventDto.builder()
                .actionType(event.getActionType())
                .userId1(event.getUserId1())
                .userId2(event.getUserId2())
                .build();

        kafkaProducer.sendEventDto(friendshipEventDto, KafkaTopics.FRIENDSHIP_EVENTS);

        log.debug("Feed friendship event published: action={}, users={}/{}",
                event.getActionType(), event.getUserId1(), event.getUserId2());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleFriendshipEvent(FriendshipDomainEvent event) {
        log.info("Publishing friendship event to Kafka: {}", event.getEventType());

        NotificationEvent notificationEvent = NotificationEvent.builder()
                .type(event.getNotificationType())
                .message(event.getMessage())
                .senderId(event.getSenderId())
                .receiverId(event.getReceiverId())
                .build();

        kafkaProducer.sendNotification(notificationEvent);

        log.debug("Friendship notification published: type={}, senderId={}, receiverId={}",
                event.getNotificationType(), event.getSenderId(), event.getReceiverId());
    }
}