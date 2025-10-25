package semsem.notificationservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import semsem.notificationservice.enums.EventType;

/**
 * DTO for comment events from main-service
 * Note: Using simplified structure, only extracting needed fields
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
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CommentData {
        private Long commentId;
        private String content;
        private AuthorData author;
        private PostData post;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AuthorData {
        private Long userId;
        private String firstName;
        private String lastName;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PostData {
        private Long postId;
        private AuthorData author;  // Post author
    }
}
