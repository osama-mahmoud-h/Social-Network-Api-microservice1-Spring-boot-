package semsem.searchservice.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import semsem.searchservice.enums.IndexType;
import java.time.Instant;


@Document(indexName = "post_index")
@Data
@Builder
public class PostIndex {

    @Id  // ✅ Use @Id for unique identifier
    private String id;
    private Long postId;

    @Field(type = FieldType.Keyword)  // ✅ Store Enums as Keyword
    private IndexType indexType;

    @Field(type = FieldType.Text, analyzer = "standard")  // ✅ Ensure full-text search works
    private String content;

    private Instant createdAt;
    private Instant updatedAt;

    @Field(type = FieldType.Long)
    private Long authorId;

    @Field(type = FieldType.Nested)
    private AppUserIndex author;
}

