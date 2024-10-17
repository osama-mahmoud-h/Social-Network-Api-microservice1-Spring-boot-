package com.example.server.dto.response;

import com.example.server.enums.ReactionType;
import lombok.*;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostResponseDto {
    private Long postId;
    private String content;
    private Long commentsCount;
    private AppUserResponseDto author;
    private ReactionType myReactionType;
    private Map<Byte,Long> userReactions;
}


