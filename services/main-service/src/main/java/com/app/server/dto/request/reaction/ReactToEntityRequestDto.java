package com.app.server.dto.request.reaction;

import com.app.server.enums.ReactionType;
import lombok.Data;

@Data
public class ReactToEntityRequestDto {
    private ReactionType reactionType;
}
