package semsem.chatservice.dto.response;


import lombok.Builder;
import lombok.Data;
import semsem.chatservice.enums.EventMessageType;

@Data
@Builder
public class EventMessageResponseDto {
    private EventMessageType eventType;
    private Object data;
}
