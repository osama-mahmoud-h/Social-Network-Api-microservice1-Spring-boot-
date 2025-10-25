package semsem.chatservice.model;

import lombok.Data;
import org.springframework.data.annotation.Id;

import semsem.chatservice.enums.UserStatus;

@Data
//@Document(collection =  "users")
public class AppUser {
    @Id
    private String id;
//    @Indexed(unique = true)
    private String nickName;
    private String password;
    private String email;
    private String phone;
    private String firstName;
    private String lastName;
    private String fullName;
    private String profilePictureUrl;
    private String bio;

    private UserStatus status;

}
