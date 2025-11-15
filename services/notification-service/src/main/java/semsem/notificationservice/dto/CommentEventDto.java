package semsem.notificationservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import semsem.notificationservice.enums.EventType;

/**
 * DTO for comment events from main-service
 * Matches the structure sent by main-service CommentEventDto
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CommentEventDto implements DomainEvent {
    private String actionType;  // CREATE, UPDATE, DELETE, REPLY
    private Long commentId;
    private CommentData comment;

    @Override
    public String getEventType() {
        return EventType.COMMENT_EVENT.name();
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CommentData {
        private Long commentId;
        private String content;
        private Long createdAt;  // Epoch seconds
        private Long updatedAt;  // Epoch seconds
        private Long postId;  // Reference to the post (not full object)
        private Long postAuthorId;  // Post author's user ID for notifications
        private Long parentCommentId;  // For replies
        private AuthorData author;  // Comment author
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AuthorData {
        private Long userId;
        private String firstName;
        private String lastName;
    }
}
