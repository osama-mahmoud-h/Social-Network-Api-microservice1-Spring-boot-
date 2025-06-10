package semsem.searchservice.dto.response;


import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Builder
@Data
public class CommentIndexResponseDto {
    private Long commentId;
    private String content;
    private Long postId;
    private Long authorId;
    private AppUserResponseDto author;
    private Long parentCommentId;
    private Instant createdAt;
    private Instant updatedAt;
}
