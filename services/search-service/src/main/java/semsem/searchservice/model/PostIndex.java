package semsem.searchservice.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.CompletionField;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.core.suggest.Completion;
import semsem.searchservice.enums.IndexType;
import java.time.Instant;


@Document(indexName = "post_index")
@Data
@Builder
public class PostIndex {
    @Id
    private String postId;

    @Field(type = FieldType.Keyword)  // ✅ Store Enums as Keyword
    private IndexType indexType;

    @Field(type = FieldType.Text, analyzer = "standard")  // ✅ Ensure full-text search works
    private String content;

    @Field(type = FieldType.Date)
    private Instant createdAt;

    @Field(type = FieldType.Date)
    private Instant updatedAt;

    @Field(type = FieldType.Long)
    private Long authorId;

    @Field(type = FieldType.Nested)
    private AppUserIndex author;

//    @CompletionField(analyzer = "simple", searchAnalyzer = "simple")
//    private Completion suggest;

}

