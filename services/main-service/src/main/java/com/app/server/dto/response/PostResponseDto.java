package com.app.server.dto.response;

import com.app.server.dto.response.user.AuthorResponseDto;
import com.app.server.enums.ReactionType;
import lombok.*;

import java.time.Instant;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostResponseDto {
    private Long postId;
    private String content;
    private Long commentsCount;
    private Long reactionsCount;
    private Instant createdAt;
    private Instant updatedAt;
    private AuthorResponseDto author;
    private ReactionType myReactionType;
    //private Map<ReactionType,Integer> reactionsTypeCount;
    private Set<FileResponseDto>files;
}


