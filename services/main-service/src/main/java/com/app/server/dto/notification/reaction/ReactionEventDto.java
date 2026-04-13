package com.app.server.dto.notification.reaction;

import com.app.server.enums.ReactionActionType;
import com.app.server.enums.ReactionTargetType;
import com.app.server.enums.ReactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReactionEventDto implements Serializable {

    private ReactionActionType actionType;
    private ReactionType reactionType;
    private ReactionTargetType targetType;
    private Long targetId;
    private Long postId;
    private Long reactorUserId;
}