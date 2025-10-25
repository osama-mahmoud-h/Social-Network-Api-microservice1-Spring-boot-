package semsem.chatservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import semsem.chatservice.dto.request.GetConversationRequestDto;
import semsem.chatservice.dto.request.NewPrivateChatMessageRequestDto;
import semsem.chatservice.dto.request.NewPublicChatMessageRequestDto;
import semsem.chatservice.dto.response.ChatMessageResponseDto;
import semsem.chatservice.mapper.ChatMessageMapper;
import semsem.chatservice.model.ChatMessage;
import semsem.chatservice.repository.ChatMessageRepository;
import semsem.chatservice.repository.RedisChatMessageRepository;
import semsem.chatservice.service.ChatMessageService;
import semsem.chatservice.service.ChatRoomService;
import semsem.chatservice.service.FriendsService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatMessageServiceImpl implements ChatMessageService {
    private final RedisChatMessageRepository redisChatMessageRepository;
    private final ChatMessageMapper chatMessageMapper;
    private final FriendsService friendsService;
    private final ChatRoomService chatRoomService;

    @Override
    public ChatMessageResponseDto saveMessage(NewPublicChatMessageRequestDto requestDto) {
        log.info("Saving new message: {}", requestDto);

        ChatMessage chatMessage = chatMessageMapper.mapNewChatMessageRequestToChatMessage(requestDto);

        // Save to Redis for fast access
        redisChatMessageRepository.saveMessage(chatMessage);

        log.debug("Message saved to both MongoDB and Redis: messageId={}", chatMessage.getMessageId());

        return chatMessageMapper.mapChatMessageToResponseDto(chatMessage);
    }


    @Override
    public ChatMessageResponseDto savePrivateMessage(NewPrivateChatMessageRequestDto requestDto, String token, Long authenticatedUserId) {
        log.info("Saving private message from userId={} to userId={}", requestDto.getSenderId(), requestDto.getReceiverId());

        // Validate sender ID matches authenticated user
        if (!requestDto.getSenderId().equals(authenticatedUserId.toString())) {
            log.warn("Sender ID mismatch: claimed={}, authenticated={}", requestDto.getSenderId(), authenticatedUserId);
            throw new RuntimeException("Unauthorized: Sender ID does not match authenticated user");
        }

        // Parse receiver ID
        Long receiverId;
        try {
            receiverId = Long.parseLong(requestDto.getReceiverId());
        } catch (NumberFormatException e) {
            log.error("Invalid receiver ID format: {}", requestDto.getReceiverId());
            throw new RuntimeException("Invalid receiver ID format");
        }

        // Check if users are friends
        boolean areFriends = friendsService.areFriends(token, authenticatedUserId, receiverId);
        if (!areFriends) {
            log.warn("Users are not friends: senderId={}, receiverId={}", authenticatedUserId, receiverId);
            throw new RuntimeException("Unauthorized: You can only send messages to friends");
        }

        // Map request to ChatMessage entity
        ChatMessage chatMessage = chatMessageMapper.mapPrivateChatMessageRequestToChatMessage(requestDto);

        // Save to Redis for fast access
        redisChatMessageRepository.saveMessage(chatMessage);
        log.debug("Private message saved to Redis: messageId={}", chatMessage.getMessageId());

        return chatMessageMapper.mapChatMessageToResponseDto(chatMessage);
    }

    @Override
    public List<ChatMessageResponseDto> getConversations(String senderId, String receiverId) {


        String chatRoomId = chatRoomService.createChatId(senderId, receiverId);
        // Try to get from Redis first (faster)
        List<ChatMessage> chatMessages = redisChatMessageRepository.getMessagesByChatId(chatRoomId);

        return chatMessages.stream()
                .map(chatMessageMapper::mapChatMessageToResponseDto)
                .collect(Collectors.toList());
    }


}
