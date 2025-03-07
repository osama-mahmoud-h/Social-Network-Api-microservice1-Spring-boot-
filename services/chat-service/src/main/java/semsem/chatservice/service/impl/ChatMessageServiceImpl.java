package semsem.chatservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import semsem.chatservice.dto.request.GetConversationRequestDto;
import semsem.chatservice.dto.request.NewPublicChatMessageRequestDto;
import semsem.chatservice.dto.response.ChatMessageResponseDto;
import semsem.chatservice.mapper.ChatMessageMapper;
import semsem.chatservice.model.ChatMessage;
import semsem.chatservice.repository.ChatMessageRepository;
import semsem.chatservice.service.ChatMessageService;
import semsem.chatservice.service.ChatRoomService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatMessageServiceImpl implements ChatMessageService {
    private final ChatMessageRepository chatMessageRepository;
    protected final ChatRoomService chatRoomService;
    private final ChatMessageMapper chatMessageMapper;

    @Override
    public ChatMessageResponseDto saveMessage(NewPublicChatMessageRequestDto requestDto) {
        System.out.println("Saving new message: " + requestDto);

        String chatRoomId = chatRoomService.getChatRoomId(requestDto.getSenderId(), requestDto.getReceiverId())
                .orElseThrow(() -> new RuntimeException("Chat room not found"));

        ChatMessage chatMessage = chatMessageMapper.mapNewChatMessageRequestToChatMessage(requestDto);
        chatMessage.setChatId(chatRoomId);

        ChatMessage savedChatMessage = chatMessageRepository.save(chatMessage);

        return chatMessageMapper.mapChatMessageToResponseDto(savedChatMessage);
    }


    @Override
    public List<ChatMessageResponseDto> getConversations(String senderId, String receiverId) {
        String chatRoomId = chatRoomService.getChatRoomId(senderId, receiverId)
                .orElseThrow(() -> new RuntimeException("Chat room not found"));

        List<ChatMessage> chatMessages = chatMessageRepository.findChatMessagesByChatId(chatRoomId);
        return chatMessages.stream()
                .map(chatMessageMapper::mapChatMessageToResponseDto)
                .collect(Collectors.toList());
    }


}
