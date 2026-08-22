package com.app.shared.events;

import com.app.shared.events.type.EventType;
import com.app.shared.events.type.PostActionType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Published on {@link KafkaTopics#POST_EVENTS} when a post is created, updated, or deleted.
 *
 * <p>Consumers: search-service (indexing), notification-service (fan-out to friends),
 * main-service (feed fan-out).
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PostEventDto implements IntegrationEvent {

    private PostActionType actionType;
    private Long postId;
    private PostData post;

    @Override
    @JsonIgnore
    public EventType getEventType() {
        return EventType.POST_EVENT;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PostData implements Serializable {
        private Long postId;
        private String content;
        /** Name of main-service's {@code PostPublicity}; carried as text so the wire does not
         *  pin a database-persisted enum. */
        private String publicity;
        /** Epoch seconds. */
        private Long createdAt;
        /** Epoch seconds. */
        private Long updatedAt;
        private AuthorData author;
    }
}