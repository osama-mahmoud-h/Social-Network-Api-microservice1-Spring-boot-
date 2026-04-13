package com.app.server.event.app.domain;

import com.app.server.enums.FriendshipActionType;
import lombok.Getter;

@Getter
public class FeedFriendshipDomainEvent extends DomainEvent {

    private final FriendshipActionType actionType;
    private final Long userId1;
    private final Long userId2;

    public FeedFriendshipDomainEvent(Long triggeredBy, FriendshipActionType actionType,
                                      Long userId1, Long userId2) {
        super(triggeredBy);
        this.actionType = actionType;
        this.userId1 = userId1;
        this.userId2 = userId2;
    }

    @Override
    public String getEventType() {
        return "FEED_FRIENDSHIP_" + actionType.name();
    }
}