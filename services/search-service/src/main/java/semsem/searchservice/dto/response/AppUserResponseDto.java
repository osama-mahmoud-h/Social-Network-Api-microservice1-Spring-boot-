package semsem.searchservice.dto.response;


import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class AppUserResponseDto {
    private Long userId;
    private String email;
    private String firstName;
    private String lastName;
    private String profilePictureUrl;
}
