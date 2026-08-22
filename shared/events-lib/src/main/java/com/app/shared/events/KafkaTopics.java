package com.app.shared.events;

import java.util.List;

/**
 * The single registry of Kafka topic names for the platform.
 *
 * <p>Deliberately a constants class rather than an enum: {@code @KafkaListener(topics = ...)}
 * requires a compile-time constant expression, so an enum cannot be referenced from the
 * annotation. That limitation is why consumers previously hardcoded topic strings while a
 * parallel (and wrong) enum drifted alongside them.
 */
public final class KafkaTopics {

    public static final String POST_EVENTS = "post-events";
    public static final String COMMENT_EVENTS = "comment-events";
    public static final String NOTIFICATION_EVENTS = "notification-events";
    public static final String LIKE_EVENTS = "like-events";
    public static final String FRIENDSHIP_EVENTS = "friendship-events";
    public static final String USER_EVENTS = "user-events";

    /** Reserved for planned features; declared so topic auto-creation stays unchanged. */
    public static final String FOLLOW_EVENTS = "follow-events";
    public static final String MESSAGE_EVENTS = "message-events";
    public static final String ACTIVITY_EVENTS = "activity-events";

    /** Every topic the platform owns — used to auto-create topics at startup. */
    public static final List<String> ALL = List.of(
            POST_EVENTS,
            COMMENT_EVENTS,
            NOTIFICATION_EVENTS,
            LIKE_EVENTS,
            FRIENDSHIP_EVENTS,
            USER_EVENTS,
            FOLLOW_EVENTS,
            MESSAGE_EVENTS,
            ACTIVITY_EVENTS
    );

    private KafkaTopics() {
    }
}