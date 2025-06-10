package semsem.chatservice.dto.response;


import lombok.Builder;
import lombok.Data;
import semsem.chatservice.enums.MessageType;

import java.time.Instant;

@Data
@Builder
public class ChatMessageResponseDto {
    private String id;
    private String chatId;
    private String senderId;
    private String receiverId;
    private String content;
    private MessageType messageType;
    private Instant timestamp;
    private boolean isSeen;
}
