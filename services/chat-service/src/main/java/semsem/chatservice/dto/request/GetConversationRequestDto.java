package semsem.chatservice.dto.request;


import lombok.Data;

@Data
public class GetConversationRequestDto {
    private String senderId;
    private String receiverId;
    private long limit;
    private long offset;
}
