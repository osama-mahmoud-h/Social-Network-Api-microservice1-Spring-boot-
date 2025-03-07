package semsem.chatservice.utils;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OnlineUserVal {
    private String sessionId;
    private String username;
    private String customUserSessionId;
    private String userId;

}
