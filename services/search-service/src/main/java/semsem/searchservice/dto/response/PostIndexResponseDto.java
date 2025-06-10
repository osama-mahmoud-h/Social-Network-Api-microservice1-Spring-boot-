package semsem.searchservice.dto.response;


import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Builder
@Data
public class PostIndexResponseDto {
    private Long postId;
    private String content;
    private Instant createdAt;
    private Instant updatedAt;
    private AppUserResponseDto author;
}
