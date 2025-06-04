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

@Document(indexName = "app_user_index")
@Data
@Builder
public class AppUserIndex {
    @Id
    private String id; // Unique identifier for the index

    private Long userId;

    @Field(type = FieldType.Text, fielddata = true)
    private IndexType indexType;

    private String email;
    private String role;
    private String status;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String firstName;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String lastName;

    private String cover;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String bio;

    private String profilePictureUrl;

}
