package com.app.server.dto.notification.post;

import com.app.server.enums.PostActionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * DTO for post events sent to Kafka
 * Contains simplified post data to avoid JPA serialization issues
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostEventDto implements Serializable {
    private PostActionType actionType;
    private Long postId;
    private PostData post;

    /**
     * Simplified post data for event serialization
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PostData implements Serializable {
        private Long postId;
        private String content;
        private Long createdAt;  // Unix timestamp in seconds
        private Long updatedAt;  // Unix timestamp in seconds
        private AuthorData author;
    }

    /**
     * Author information embedded in post data
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
