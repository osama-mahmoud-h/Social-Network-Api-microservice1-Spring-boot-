package com.app.server.event.app.domain;

import com.app.server.enums.ReactionActionType;
import com.app.server.enums.ReactionTargetType;
import com.app.server.enums.ReactionType;
import lombok.Getter;

@Getter
public class ReactionDomainEvent extends DomainEvent {

    private final ReactionActionType actionType;
    private final ReactionType reactionType;
    private final ReactionTargetType targetType;
    private final Long targetId;
    private final Long reactorUserId;
    private final Long postId;

    public ReactionDomainEvent(Long reactorUserId, ReactionActionType actionType,
                               ReactionType reactionType, ReactionTargetType targetType,
                               Long targetId, Long postId) {
        super(reactorUserId);
        this.reactorUserId = reactorUserId;
        this.actionType = actionType;
        this.reactionType = reactionType;
        this.targetType = targetType;
        this.targetId = targetId;
        this.postId = postId;
    }

    @Override
    public String getEventType() {
        return "REACTION_" + actionType.name();
    }
}