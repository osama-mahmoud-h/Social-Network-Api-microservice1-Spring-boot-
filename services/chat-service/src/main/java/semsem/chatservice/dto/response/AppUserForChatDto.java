package semsem.chatservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppUserForChatDto {
    private Long userId;
    private String username;
    private String email;
    private String imageUrl;
}