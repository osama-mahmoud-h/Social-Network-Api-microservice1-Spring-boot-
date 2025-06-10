package semsem.chatservice.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document
public class ChatRoom {
    @Id
    private String chatRoomId;
    private String chatId;
    private String senderId;
    private String receiverId;
    private Instant createdAt;
}
