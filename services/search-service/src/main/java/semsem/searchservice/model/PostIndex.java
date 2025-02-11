package semsem.searchservice.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.time.Instant;


@Document(indexName = "post_index")
@Data
public class PostIndex {
    @Id
    private String postId;
    private String content;
    private Instant createdAt;
    private Instant updatedAt;
    private Long authorId;
}
