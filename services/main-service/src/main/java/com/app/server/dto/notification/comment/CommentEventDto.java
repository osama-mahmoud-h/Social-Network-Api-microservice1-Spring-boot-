package com.app.server.dto.notification.comment;

import com.app.server.enums.CommentActionType;
import lombok.*;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentEventDto implements Serializable {
    private CommentActionType actionType;
    private Long commentId;
    private CommentData comment;

    /**
     * Simplified comment data for Kafka serialization
     * Contains only the essential fields needed by consumers
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CommentData implements Serializable {
        private Long commentId;
        private String content;
        private Long createdAt;  // Epoch seconds
        private Long updatedAt;  // Epoch seconds
        private Long postId;
        private Long parentCommentId;
        private AuthorData author;
    }

    /**
     * Simplified author data for serialization
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AuthorData implements Serializable {
        private Long userId;
        private String firstName;
        private String lastName;
    }
}
