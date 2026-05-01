package semsem.chatservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import semsem.chatservice.dto.request.NewPrivateChatMessageRequestDto;
import semsem.chatservice.dto.request.NewPublicChatMessageRequestDto;
import semsem.chatservice.dto.response.ChatMessageResponseDto;
import semsem.chatservice.enums.ConversationType;
import semsem.chatservice.event.ChatMessageEvent;
import semsem.chatservice.event.ChatMessageKafkaProducer;
import semsem.chatservice.mapper.ChatMessageMapper;
import semsem.chatservice.model.ChatMessage;
import semsem.chatservice.repository.RedisChatMessageRepository;
import semsem.chatservice.service.ChatMessageService;
import semsem.chatservice.service.ChatRoomService;
import semsem.chatservice.service.FriendsService;
import semsem.chatservice.utils.SnowflakeIdGenerator;

import java.time.Instant;
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
    private final SnowflakeIdGenerator snowflakeIdGenerator;
    private final ChatMessageKafkaProducer kafkaProducer;

    @Override
    public ChatMessageResponseDto saveMessage(NewPublicChatMessageRequestDto requestDto) {
        ChatMessage chatMessage = chatMessageMapper.mapNewChatMessageRequestToChatMessage(requestDto);
        chatMessage.setMessageId(snowflakeIdGenerator.nextId());
        chatMessage.setTimestamp(Instant.now());

        publishToKafka(chatMessage, ConversationType.PRIVATE, null);
        redisChatMessageRepository.saveMessage(chatMessage); // dual-write during Phase 1

        return chatMessageMapper.mapChatMessageToResponseDto(chatMessage);
    }

    @Override
    public ChatMessageResponseDto savePrivateMessage(NewPrivateChatMessageRequestDto requestDto,
                                                     String token, Long authenticatedUserId) {
        if (!requestDto.getSenderId().equals(authenticatedUserId.toString())) {
            throw new RuntimeException("Unauthorized: Sender ID does not match authenticated user");
        }

        Long receiverId;
        try {
            receiverId = Long.parseLong(requestDto.getReceiverId());
        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid receiver ID format");
        }

        if (!friendsService.areFriends(token, authenticatedUserId, receiverId)) {
            throw new RuntimeException("Unauthorized: You can only send messages to friends");
        }

        ChatMessage chatMessage = chatMessageMapper.mapPrivateChatMessageRequestToChatMessage(requestDto);
        chatMessage.setMessageId(snowflakeIdGenerator.nextId());
        chatMessage.setTimestamp(Instant.now());
        chatMessage.setChatId(chatRoomService.createChatId(requestDto.getSenderId(), requestDto.getReceiverId()));

        publishToKafka(chatMessage, ConversationType.PRIVATE, null);
        redisChatMessageRepository.saveMessage(chatMessage); // dual-write during Phase 1

        log.debug("Private message queued: messageId={}", chatMessage.getMessageId());
        return chatMessageMapper.mapChatMessageToResponseDto(chatMessage);
    }

    @Override
    public List<ChatMessageResponseDto> getConversations(String senderId, String receiverId) {
        String conversationId = chatRoomService.createChatId(senderId, receiverId);
        List<ChatMessage> messages = redisChatMessageRepository.getMessagesByChatId(conversationId);
        return messages.stream()
                .map(chatMessageMapper::mapChatMessageToResponseDto)
                .collect(Collectors.toList());
    }

    private void publishToKafka(ChatMessage message, ConversationType type, String groupId) {
        ChatMessageEvent event = ChatMessageEvent.builder()
                .messageId(message.getMessageId())
                .conversationId(message.getChatId())
                .senderId(message.getSenderId())
                .receiverId(message.getReceiverId())
                .groupId(groupId)
                .content(message.getContent())
                .messageType(message.getMessageType())
                .conversationType(type)
                .createdAtEpochMs(message.getTimestamp().toEpochMilli())
                .build();
        kafkaProducer.publish(event);
    }
}
