package semsem.searchservice.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;
import org.springframework.data.elasticsearch.core.suggest.Completion;
import semsem.searchservice.enums.IndexType;

import java.time.Instant;

@Document(indexName = "comment_index")
@Data
@Builder
public class CommentIndex {
    @Id // Use @Id for unique identifier
    private String id; // This will be the unique identifier for the comment index
    private Long commentId;
    @Field(type = FieldType.Text, fielddata = true)
    private IndexType indexType;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String content;
    private Long postId;
    private Long authorId;
    private Long parentCommentId;

    private Instant createdAt;
    private Instant updatedAt;

    //nest appUserIndex
    @Field(type = FieldType.Nested)
    private AppUserIndex author;

//    @CompletionField(analyzer = "simple", searchAnalyzer = "simple")
//    private Completion suggest;
}
