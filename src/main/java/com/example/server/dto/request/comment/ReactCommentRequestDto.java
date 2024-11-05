package com.example.server.dto.request.comment;

import com.example.server.enums.ReactionType;
import lombok.Data;

@Data
public class ReactCommentRequestDto {
    private Long commentDto;
    private ReactionType reactionType;
}
