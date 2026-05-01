package semsem.chatservice.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import semsem.chatservice.enums.ConversationType;
import semsem.chatservice.enums.MessageType;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageEvent {
    private long        messageId;       // Snowflake ID
    private String      conversationId;  // partition key — determines Kafka partition
    private String      senderId;
    private String      receiverId;      // null for group messages
    private String      groupId;         // null for 1:1 messages
    private String      content;
    private MessageType messageType;
    private ConversationType conversationType;
    private long        createdAtEpochMs;
}