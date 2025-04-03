package com.app.server.enums;

import lombok.Getter;

@Getter
public enum KafkaTopics {
    POST_EVENTS("post-events"),
    USER_EVENTS("user-events"),
    NOTIFICATION_EVENTS("notification-events"),
    COMMENT_EVENTS("comment-events"),
    LIKE_EVENTS("like-events"),
    FOLLOW_EVENTS("follow-events"),
    MESSAGE_EVENTS("message-events"),
    ACTIVITY_EVENTS("activity-events"),
    ;

    private final String value;
    KafkaTopics(String value) {
        this.value = value;
    }

}
