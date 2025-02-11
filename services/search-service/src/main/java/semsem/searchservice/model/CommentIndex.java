package semsem.searchservice.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.time.Instant;

@Document(indexName = "comment_index")
@Data
public class CommentIndex {
    @Id
    private String commentId;
    private String content;
    private Long postId;
    private Long authorId;
    private Long parentCommentId;
    private Instant createdAt;
    private Instant updatedAt;
}
