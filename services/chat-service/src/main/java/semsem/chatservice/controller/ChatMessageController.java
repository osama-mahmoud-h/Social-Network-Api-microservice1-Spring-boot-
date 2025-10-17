package semsem.chatservice.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import semsem.chatservice.dto.request.ChatRoomMessageDto;
import semsem.chatservice.dto.request.NewPrivateChatMessageRequestDto;
import semsem.chatservice.dto.request.NewPublicChatMessageRequestDto;
import semsem.chatservice.dto.response.ChatMessageResponseDto;
import semsem.chatservice.security.WebSocketAuthenticationHelper;
import semsem.chatservice.service.ChatMessageService;

import java.util.List;
import java.util.Objects;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatMessageController {
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ChatMessageService chatMessageService;
    private final  WebSocketAuthenticationHelper webSocketAuthenticationHelper;

   @MessageMapping("/chat")
    public void saveNewMessage(
            @Payload NewPublicChatMessageRequestDto requestDto,
            SimpMessageHeaderAccessor headerAccessor) {

        // Extract authenticated user data from WebSocket session using helper
        Long userId = webSocketAuthenticationHelper.getUserId(headerAccessor);
        String email = webSocketAuthenticationHelper.getEmail(headerAccessor);

        log.info("Message received from authenticated user - ID: {}, Email: {}", userId, email);

        // You can now use userId instead of relying on the client to send it
        // This prevents users from impersonating other users

        ChatMessageResponseDto savedMessage = chatMessageService.saveMessage(requestDto);
        simpMessagingTemplate.convertAndSendToUser(savedMessage.getReceiverId(),"/queue/messages", savedMessage);
    }

    @GetMapping("/messages/{senderId}/{receiverId}")
    public ResponseEntity<List<ChatMessageResponseDto>> getConversations(
            @PathVariable("senderId") String senderId,
            @PathVariable("receiverId") String receiverId
    ) {
        return ResponseEntity.ok(chatMessageService.getConversations(senderId, receiverId));
    }

    @MessageMapping("/message.sendPrivateMessage")
    public void handlePrivateMessage(
            @Payload NewPrivateChatMessageRequestDto msg,
            SimpMessageHeaderAccessor headerAccessor) {

        // Extract authenticated user data from WebSocket session using helper
        Long authenticatedUserId = webSocketAuthenticationHelper.getUserId(headerAccessor);
        String email = webSocketAuthenticationHelper.getEmail(headerAccessor);

        log.info("Private message from user ID: {}, Email: {} to receiver: {}",
                authenticatedUserId, email, msg.getReceiverId());

        // Security: Verify the sender ID matches the authenticated user
        // This prevents users from spoofing messages as other users
        if (!webSocketAuthenticationHelper.verifySender(headerAccessor, msg.getSenderId())) {
            log.warn("User {} attempted to send message as user {}", authenticatedUserId, msg.getSenderId());
            return; // Reject the message
        }

        simpMessagingTemplate.convertAndSendToUser(msg.getReceiverId(), "/private", msg);
    }

    /**
     * Handle public chat room messages
     * Endpoint: /app/chat.sendMessage
     * Broadcasts to: /topic/chat/{room}
     */
    @MessageMapping("/chat.sendMessage")
    public void handlePublicChatMessage(
            @Payload ChatRoomMessageDto messageDto,
            SimpMessageHeaderAccessor headerAccessor) {

        // Extract authenticated user data from WebSocket session
        Long authenticatedUserId = webSocketAuthenticationHelper.getUserId(headerAccessor);
        String email = webSocketAuthenticationHelper.getEmail(headerAccessor);

        log.info("Public chat message from user: {} (ID: {}) in room: {}",
                email, authenticatedUserId, messageDto.getRoom());

        // Add timestamp
        messageDto.setTimestamp(java.time.Instant.now().toString());

        // Broadcast message to all users subscribed to this room
        simpMessagingTemplate.convertAndSend(
                "/topic/chat/" + messageDto.getRoom(),
                messageDto
        );
    }

    /**
     * Handle user joining a chat room
     * Endpoint: /app/chat.addUser
     * Broadcasts to: /topic/chat/{room}
     */
    @MessageMapping("/chat.addUser")
    public void handleUserJoin(
            @Payload ChatRoomMessageDto messageDto,
            SimpMessageHeaderAccessor headerAccessor) {

        // Extract authenticated user data
        Long authenticatedUserId = webSocketAuthenticationHelper.getUserId(headerAccessor);
        String email = webSocketAuthenticationHelper.getEmail(headerAccessor);

        // Store user info in WebSocket session
        Objects.requireNonNull(headerAccessor.getSessionAttributes()).put("username", messageDto.getSender());
        headerAccessor.getSessionAttributes().put("room", messageDto.getRoom());

        log.info("User joined room: {} - User: {} (ID: {})",
                messageDto.getRoom(), email, authenticatedUserId);

        // Add timestamp
        messageDto.setTimestamp(java.time.Instant.now().toString());
        messageDto.setContent(messageDto.getSender() + " joined the chat");

        // Broadcast join message to room
        simpMessagingTemplate.convertAndSend(
                "/topic/chat/" + messageDto.getRoom(),
                messageDto
        );
    }

    /**
     * Handle user leaving a chat room
     * Endpoint: /app/chat.removeUser
     * Broadcasts to: /topic/chat/{room}
     */
    @MessageMapping("/chat.removeUser")
    public void handleUserLeave(
            @Payload ChatRoomMessageDto messageDto,
            SimpMessageHeaderAccessor headerAccessor) {

        // Extract authenticated user data
        Long authenticatedUserId = webSocketAuthenticationHelper.getUserId(headerAccessor);
        String email = webSocketAuthenticationHelper.getEmail(headerAccessor);

        log.info("User left room: {} - User: {} (ID: {})",
                messageDto.getRoom(), email, authenticatedUserId);

        // Add timestamp
        messageDto.setTimestamp(java.time.Instant.now().toString());
        messageDto.setContent(messageDto.getSender() + " left the chat");

        // Broadcast leave message to room
        simpMessagingTemplate.convertAndSend(
                "/topic/chat/" + messageDto.getRoom(),
                messageDto
        );
    }
}
