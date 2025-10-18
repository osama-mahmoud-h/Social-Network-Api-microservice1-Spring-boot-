package semsem.chatservice.service;

import semsem.chatservice.dto.request.GetConversationRequestDto;
import semsem.chatservice.dto.request.NewPrivateChatMessageRequestDto;
import semsem.chatservice.dto.request.NewPublicChatMessageRequestDto;
import semsem.chatservice.dto.response.ChatMessageResponseDto;

import java.util.List;

public interface ChatMessageService {
    ChatMessageResponseDto saveMessage(NewPublicChatMessageRequestDto requestDto);

    ChatMessageResponseDto savePrivateMessage(NewPrivateChatMessageRequestDto requestDto, String token, Long authenticatedUserId);

    List<ChatMessageResponseDto> getConversations(String senderId, String receiverId);
}
