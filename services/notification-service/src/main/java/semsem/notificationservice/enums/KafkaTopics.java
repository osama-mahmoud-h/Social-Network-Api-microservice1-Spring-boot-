package semsem.notificationservice.enums;

import lombok.Getter;

@Getter
public enum KafkaTopics {
    POST_EVENTS("post_events"),
    USER_EVENTS("user-events"),
    NOTIFICATION_EVENTS("notification_events"),
    COMMENT_EVENTS("comment_events"),
    LIKE_EVENTS("like_events"),
    FOLLOW_EVENTS("follow_events"),
    MESSAGE_EVENTS("message_events"),
    ACTIVITY_EVENTS("activity_events"),
    ;

    private final String value;

    KafkaTopics(String value) {
        this.value = value;
    }

}
