package semsem.searchservice.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.CompletionField;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.core.suggest.Completion;
import semsem.searchservice.enums.IndexType;

import java.time.Instant;

@Document(indexName = "comment_index")
@Data
public class CommentIndex {
    @Id
    private String commentId;
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
