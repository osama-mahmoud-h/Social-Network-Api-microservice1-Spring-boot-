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
import semsem.chatservice.dto.request.NewPrivateChatMessageRequestDto;
import semsem.chatservice.dto.request.NewPublicChatMessageRequestDto;
import semsem.chatservice.dto.request.TypingEventRequestDto;
import semsem.chatservice.dto.response.AppUserForChatDto;
import semsem.chatservice.dto.response.ChatMessageResponseDto;
import semsem.chatservice.repository.RedisOnlineUserRepository;
import semsem.chatservice.security.WebSocketAuthenticationHelper;
import semsem.chatservice.service.ChatMessageService;
import semsem.chatservice.service.FriendsService;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatMessageController {
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ChatMessageService chatMessageService;
    private final WebSocketAuthenticationHelper webSocketAuthenticationHelper;
    private final FriendsService friendsService;
    private final RedisOnlineUserRepository redisOnlineUserRepository;

    /**
     * Handle private messages between friends
     * Endpoint: /app/private.sendMessage
     * Validates friendship before sending
     */
    @MessageMapping("/private.sendMessage")
    public void handlePrivateMessage(
            @Payload NewPrivateChatMessageRequestDto msg,
            SimpMessageHeaderAccessor headerAccessor) {

        // Extract authenticated user data from WebSocket session
        Long authenticatedUserId = webSocketAuthenticationHelper.getUserId(headerAccessor);
        String email = webSocketAuthenticationHelper.getEmail(headerAccessor);
        String token = webSocketAuthenticationHelper.getToken(headerAccessor);

        log.info("Private message from user ID: {}, Email: {} to receiver: {}",
                authenticatedUserId, email, msg.getReceiverId());

        // Security: Verify the sender ID matches the authenticated user
        if (!webSocketAuthenticationHelper.verifySender(headerAccessor, msg.getSenderId())) {
            log.warn("User {} attempted to send message as user {}", authenticatedUserId, msg.getSenderId());
            return; // Reject the message
        }

        try {
            // Save message with friend validation
            ChatMessageResponseDto savedMessage = chatMessageService.savePrivateMessage(
                    msg,
                    "Bearer " + token,
                    authenticatedUserId
            );

            // Send to receiver
            simpMessagingTemplate.convertAndSendToUser(
                    msg.getReceiverId(),
                    "/queue/messages",
                    savedMessage
            );

            // Send confirmation to sender
            simpMessagingTemplate.convertAndSendToUser(
                    msg.getSenderId(),
                    "/queue/messages",
                    savedMessage
            );

            log.info("Private message sent successfully: messageId={}", savedMessage.getId());
        } catch (RuntimeException e) {
            log.error("Failed to send private message: {}", e.getMessage());
            // Could send error message back to sender
            simpMessagingTemplate.convertAndSendToUser(
                    msg.getSenderId(),
                    "/queue/errors",
                    e.getMessage()
            );
        }
    }

    /**
     * REST endpoint to get conversation history between two users
     * GET /api/messages/{senderId}/{receiverId}
     */
    @GetMapping("/api/messages/{senderId}/{receiverId}")
    public ResponseEntity<List<ChatMessageResponseDto>> getConversations(
            @PathVariable("senderId") String senderId,
            @PathVariable("receiverId") String receiverId
    ) {
        log.info("Fetching conversation history between {} and {}", senderId, receiverId);
        return ResponseEntity.ok(chatMessageService.getConversations(senderId, receiverId));
    }

    /**
     * REST endpoint to send a private message
     * POST /api/messages/send
     */
    @PostMapping("/api/messages/send")
    public ResponseEntity<?> sendPrivateMessage(
            @RequestBody NewPrivateChatMessageRequestDto requestDto,
            @RequestHeader("Authorization") String token,
            @RequestHeader("X-User-Id") Long authenticatedUserId
    ) {
        log.info("REST: Sending private message from userId={} to userId={}", requestDto.getSenderId(), requestDto.getReceiverId());

        try {
            ChatMessageResponseDto savedMessage = chatMessageService.savePrivateMessage(
                    requestDto,
                    token,
                    authenticatedUserId
            );

            return ResponseEntity.ok(savedMessage);
        } catch (RuntimeException e) {
            log.error("Failed to send private message: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * WebSocket endpoint to check which friends are online
     * Endpoint: /app/friends.checkOnline
     * Client sends list of friend IDs and receives back which ones are online
     */
    @MessageMapping("/friends.checkOnline")
    public void checkOnlineFriends(
            @Payload List<String> friendIds,
            SimpMessageHeaderAccessor headerAccessor) {

        Long authenticatedUserId = webSocketAuthenticationHelper.getUserId(headerAccessor);

        if (authenticatedUserId == null) {
            log.warn("Cannot check online friends: user not authenticated");
            return;
        }

        log.info("Checking online status for {} friends for userId: {}", friendIds.size(), authenticatedUserId);
        try {
            // Filter friends who are online
            Set<String> onlineFriendIds = redisOnlineUserRepository.filterOnlineUsers(friendIds);

            log.info("User {} has {} online friends out of {} total friends",
                    authenticatedUserId, onlineFriendIds.size(), friendIds.size());

            // Send online friends list back to the user
            simpMessagingTemplate.convertAndSendToUser(
                    authenticatedUserId.toString(),
                    "/queue/online-friends",
                    onlineFriendIds
            );

        } catch (Exception e) {
            log.error("Error checking online friends for user {}: {}", authenticatedUserId, e.getMessage(), e);
        }
    }

    /**
     * Handle typing indicator events
     * Endpoint: /app/private.typing
     * Sends typing status to the receiver
     */
    @MessageMapping("/private.typing")
    public void handleTypingEvent(
            @Payload TypingEventRequestDto typingEvent,
            SimpMessageHeaderAccessor headerAccessor) {

        // Extract authenticated user data from WebSocket session
        Long authenticatedUserId = webSocketAuthenticationHelper.getUserId(headerAccessor);

        log.info("Typing event from user ID: {} isTyping: {}",
                authenticatedUserId, typingEvent);

        // Security: Verify the sender ID matches the authenticated user
        if (!webSocketAuthenticationHelper.verifySender(headerAccessor, typingEvent.getSenderId())) {
            log.warn("User {} attempted to send typing event as user {}",
                    authenticatedUserId, typingEvent.getSenderId());
            return; // Reject the typing event
        }

        try {
            // Send typing event to receiver only
            log.debug("Sending typing event from {} to {}",
                    typingEvent.getSenderId(), typingEvent.getReceiverId());
            simpMessagingTemplate.convertAndSendToUser(
                    typingEvent.getReceiverId(),
                    "/queue/typing",
                    typingEvent
            );

            log.debug("Typing event sent successfully from {} to {}",
                    typingEvent.getSenderId(), typingEvent.getReceiverId());
        } catch (Exception e) {
            log.error("Failed to send typing event: {}", e.getMessage());
        }
    }
}
