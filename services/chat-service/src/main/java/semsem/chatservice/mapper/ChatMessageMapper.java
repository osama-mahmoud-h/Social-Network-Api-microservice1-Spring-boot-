package semsem.chatservice.mapper;


import org.springframework.stereotype.Component;
import semsem.chatservice.dto.request.NewPrivateChatMessageRequestDto;
import semsem.chatservice.dto.request.NewPublicChatMessageRequestDto;
import semsem.chatservice.dto.response.ChatMessageResponseDto;
import semsem.chatservice.model.ChatMessage;

@Component
public class ChatMessageMapper {

    public ChatMessage mapNewChatMessageRequestToChatMessage(NewPublicChatMessageRequestDto newChatMessageRequestDto) {
        return ChatMessage.builder()
                        .senderId(newChatMessageRequestDto.getSenderId())
                        .receiverId(newChatMessageRequestDto.getReceiverId())
                        .content(newChatMessageRequestDto.getContent())
                        .messageType(newChatMessageRequestDto.getMessageType())
                        .build();
    }

    public ChatMessage mapPrivateChatMessageRequestToChatMessage(NewPrivateChatMessageRequestDto requestDto) {
        return ChatMessage.builder()
                        .senderId(requestDto.getSenderId())
                        .receiverId(requestDto.getReceiverId())
                        .content(requestDto.getContent())
                        .messageType(requestDto.getMessageType())
                        .build();
    }

    // to responseDto

    public ChatMessageResponseDto mapChatMessageToResponseDto(ChatMessage chatMessage) {
        return ChatMessageResponseDto.builder()
                        .id(chatMessage.getMessageId())
                        .senderId(chatMessage.getSenderId())
                        .receiverId(chatMessage.getReceiverId())
                        .content(chatMessage.getContent())
                        .messageType(chatMessage.getMessageType())
                        .isSeen(chatMessage.isSeen())
                        .timestamp(chatMessage.getTimestamp())
                        .build();
    }
}
