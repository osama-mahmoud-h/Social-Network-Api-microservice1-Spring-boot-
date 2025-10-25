package com.app.server.event.domain;

import com.app.server.enums.NotificationType;
import lombok.Getter;

/**
 * Domain event for friendship-related actions
 */
@Getter
public class FriendshipDomainEvent extends DomainEvent {
    private final NotificationType notificationType;
    private final Long senderId;
    private final Long receiverId;
    private final String message;

    public FriendshipDomainEvent(Long userId, NotificationType notificationType,
                                  Long senderId, Long receiverId, String message) {
        super(userId);
        this.notificationType = notificationType;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.message = message;
    }

    @Override
    public String getEventType() {
        return "FRIENDSHIP_" + notificationType.name();
    }
}
