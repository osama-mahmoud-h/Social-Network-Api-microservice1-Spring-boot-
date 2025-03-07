package semsem.chatservice.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import semsem.chatservice.enums.MessageType;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document
public class ChatMessage {
    @Id
    private String messageId;
    private String chatId;
    private String senderId;
    private String receiverId;
    private String content;
    private MessageType messageType;
    private boolean isSeen;
    private Instant timestamp;
}
