package com.app.shared.events;

import com.app.shared.events.type.EventType;
import com.app.shared.events.type.ReactionActionType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Published on {@link KafkaTopics#LIKE_EVENTS} when a user reacts to a post or comment.
 *
 * <p>{@code reactionType} and {@code targetType} are carried as text rather than enums: their
 * enum forms are persisted in main-service's {@code reactions} table, and pinning a
 * database-persisted enum to the wire contract would make every vocabulary change a migration.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReactionEventDto implements IntegrationEvent {

    private ReactionActionType actionType;
    /** Name of main-service's {@code ReactionType}, e.g. {@code LIKE}. */
    private String reactionType;
    /** Name of main-service's {@code ReactionTargetType}: {@code POST} or {@code COMMENT}. */
    private String targetType;
    private Long targetId;
    private Long postId;
    private Long reactorUserId;

    @Override
    @JsonIgnore
    public EventType getEventType() {
        return EventType.LIKE_EVENT;
    }
}