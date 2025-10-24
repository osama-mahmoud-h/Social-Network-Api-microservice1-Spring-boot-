package semsem.chatservice.dto.request;

import lombok.Data;

@Data
public class TypingEventRequestDto {
    private String senderId;
    private String receiverId;
    private String currentMessage;
    private boolean typing;
}