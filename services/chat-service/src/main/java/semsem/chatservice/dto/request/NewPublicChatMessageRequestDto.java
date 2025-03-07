package semsem.chatservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import semsem.chatservice.enums.MessageType;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NewPublicChatMessageRequestDto {
    private String chatId;
    private String senderId;
    private String receiverId;
    private String content;
    private MessageType messageType;
}
