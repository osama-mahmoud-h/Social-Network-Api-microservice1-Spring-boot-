package com.app.shared.events;

import com.app.shared.events.type.CommentActionType;
import com.app.shared.events.type.EventType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Published on {@link KafkaTopics#COMMENT_EVENTS} when a comment is created, updated, deleted,
 * or posted as a reply.
 *
 * <p>Consumers: search-service (indexing), notification-service (notify the post author),
 * main-service (feed activity).
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CommentEventDto implements IntegrationEvent {

    private CommentActionType actionType;
    private Long commentId;
    private CommentData comment;

    @Override
    @JsonIgnore
    public EventType getEventType() {
        return EventType.COMMENT_EVENT;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CommentData implements Serializable {
        private Long commentId;
        private String content;
        /** Epoch seconds. */
        private Long createdAt;
        /** Epoch seconds. */
        private Long updatedAt;
        private Long postId;
        /** Denormalised so consumers can notify the post author without a callback. */
        private Long postAuthorId;
        /** Null unless this comment is a reply. */
        private Long parentCommentId;
        private AuthorData author;
    }
}