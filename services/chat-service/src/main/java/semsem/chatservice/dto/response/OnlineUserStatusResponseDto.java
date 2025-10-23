package semsem.chatservice.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OnlineUserStatusResponseDto {
    private Long userId;
    private boolean isOnline;
}
