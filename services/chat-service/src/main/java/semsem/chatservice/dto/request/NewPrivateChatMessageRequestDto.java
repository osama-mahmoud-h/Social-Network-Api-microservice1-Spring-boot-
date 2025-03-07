package semsem.chatservice.dto.request;


import lombok.Data;
import org.springframework.data.mongodb.core.messaging.Message;
import semsem.chatservice.enums.MessageType;

@Data
public class NewPrivateChatMessageRequestDto {
    private String chatId;
    private String senderId;
    private String receiverId;
    private String content;
    private MessageType messageType;
}
