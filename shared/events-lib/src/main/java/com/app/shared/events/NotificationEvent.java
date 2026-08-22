package com.app.shared.events;

import com.app.shared.events.type.EventType;
import com.app.shared.events.type.NotificationType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Published on {@link KafkaTopics#NOTIFICATION_EVENTS} when something happens that a specific
 * user should be told about. Unlike the other events in this module, the message text is
 * composed by the producer.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class NotificationEvent implements IntegrationEvent {

    private NotificationType type;
    private String message;
    private Long senderId;
    private Long receiverId;

    @Override
    @JsonIgnore
    public EventType getEventType() {
        return EventType.NOTIFICATION_EVENT;
    }
}