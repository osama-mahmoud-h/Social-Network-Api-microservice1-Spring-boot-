package semsem.chatservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for public chat room messages
 * Used for broadcasting messages to all users in a room
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoomMessageDto {
    private String sender;
    private String content;
    private String type;  // CHAT, JOIN, LEAVE
    private String room;
    private String timestamp;
}