package com.app.shared.events;

import com.app.shared.events.type.EventType;
import com.app.shared.events.type.FriendshipActionType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Published on {@link KafkaTopics#FRIENDSHIP_EVENTS} when a friendship is accepted, removed,
 * or blocked, so consumers can rebuild the follower graph their feeds depend on.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class FriendshipEventDto implements IntegrationEvent {

    private FriendshipActionType actionType;
    private Long userId1;
    private Long userId2;

    @Override
    @JsonIgnore
    public EventType getEventType() {
        return EventType.FRIENDSHIP_EVENT;
    }
}