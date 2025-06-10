package com.app.server.dto.request.comment;

import com.app.server.enums.ReactionType;
import lombok.Data;

@Data
public class ReactCommentRequestDto {
    private Long commentDto;
    private ReactionType reactionType;
}
