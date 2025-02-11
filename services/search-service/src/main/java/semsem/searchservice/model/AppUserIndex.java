package semsem.searchservice.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "app_user_index")
@Data
public class AppUserIndex {
    @Id
    private String userId;
    private String email;
    private String role;
    private String status;
    private String firstName;
    private String lastName;
    private String cover;
    private String bio;
    private String location;
    private String website;
    private String birthday;
}
