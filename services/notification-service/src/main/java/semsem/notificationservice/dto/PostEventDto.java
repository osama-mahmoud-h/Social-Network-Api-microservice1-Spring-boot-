package semsem.notificationservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import semsem.notificationservice.enums.EventType;

/**
 * DTO for post events from main-service
 * Note: Using simplified structure, only extracting needed fields
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PostEventDto implements DomainEvent {
    private String actionType;  // CREATE, UPDATE, DELETE
    private Long postId;
    private PostData post;

    @Override
    public String getEventType() {
        return EventType.POST_EVENT.name();
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PostData {
        private Long postId;
        private String content;
        private AuthorData author;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AuthorData {
        private Long userId;
        private String firstName;
        private String lastName;
    }
}
