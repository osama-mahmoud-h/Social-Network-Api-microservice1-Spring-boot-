package semsem.chatservice.mapper;

import org.springframework.stereotype.Component;
import semsem.chatservice.event.ChatMessageEvent;
import semsem.chatservice.model.ChatMessage;
import semsem.chatservice.model.ConversationMessage;

import java.time.Instant;

@Component
public class ChatMessageEventMapper {

    public ConversationMessage toConversationMessage(ChatMessageEvent event) {
        return ConversationMessage.builder()
                .conversationId(event.getConversationId())
                .messageId(event.getMessageId())
                .senderId(event.getSenderId())
                .receiverId(event.getReceiverId())
                .content(event.getContent())
                .messageType(event.getMessageType() != null ? event.getMessageType().name() : null)
                .conversationType(event.getConversationType() != null ? event.getConversationType().name() : null)
                .createdAt(Instant.ofEpochMilli(event.getCreatedAtEpochMs()))
                .build();
    }

    public ChatMessage toChatMessage(ChatMessageEvent event) {
        return ChatMessage.builder()
                .messageId(event.getMessageId())
                .chatId(event.getConversationId())
                .senderId(event.getSenderId())
                .receiverId(event.getReceiverId())
                .content(event.getContent())
                .messageType(event.getMessageType())
                .timestamp(Instant.ofEpochMilli(event.getCreatedAtEpochMs()))
                .build();
    }
}