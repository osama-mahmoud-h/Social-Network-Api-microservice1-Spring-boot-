package semsem.chatservice.listener;




import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import semsem.chatservice.client.MainServiceClient;
import semsem.chatservice.dto.response.AppUserForChatDto;
import semsem.chatservice.dto.response.EventMessageResponseDto;
import semsem.chatservice.dto.response.MyApiResponse;
import semsem.chatservice.dto.response.OnlineUserStatusResponseDto;
import semsem.chatservice.enums.EventMessageType;
import semsem.chatservice.repository.RedisOnlineUserRepository;
import semsem.chatservice.security.WebSocketAuthenticationHelper;
import semsem.chatservice.service.ActiveUserService;
import semsem.chatservice.utils.OnlineUserVal;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketEventListener {
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final WebSocketAuthenticationHelper webSocketAuthenticationHelper;
    private final RedisOnlineUserRepository redisOnlineUserRepository;
    private final MainServiceClient mainServiceClient;
    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        log.info("New WebSocket connection established - sessionId: {}", headerAccessor.getSessionId());

        Long userId = webSocketAuthenticationHelper.getUserId(headerAccessor);
        String token = webSocketAuthenticationHelper.getToken(headerAccessor);

        if (Objects.nonNull(userId)) {
            redisOnlineUserRepository.addOnlineUser(userId.toString());
            long onlineUsersCount = redisOnlineUserRepository.getOnlineUserCount();

            log.info("User connected - userId: {}, sessionId: {}, total online users: {}",
                    userId, headerAccessor.getSessionId(), onlineUsersCount);

            // Get current online users list
            Set<String> onlineUsers = redisOnlineUserRepository.getAllOnlineUsers();
            broadcastUserConnectionStatus(userId , true);

        } else {
            log.warn("WebSocket connection established without valid user info - sessionId: {}",
                    headerAccessor.getSessionId());
        }
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        Long userId = webSocketAuthenticationHelper.getUserId(headerAccessor);
        String token = webSocketAuthenticationHelper.getToken(headerAccessor);

        if (Objects.nonNull(userId)) {
            redisOnlineUserRepository.removeOnlineUser(userId.toString());
            long onlineUsersCount = redisOnlineUserRepository.getOnlineUserCount();
            log.info("User disconnected - userId: {}, sessionId: {}, total online users: {}",
                    userId, headerAccessor.getSessionId(), onlineUsersCount);

        } else {
            log.warn("WebSocket disconnection without valid user info - sessionId: {}",
                    headerAccessor.getSessionId());
        }
        // Get current online users list
        Set<String> onlineUsers = redisOnlineUserRepository.getAllOnlineUsers();
        broadcastUserConnectionStatus(userId , false);
    }

    /**
     * Notify only online friends about a user's status change (online/offline)
     * @param userId The ID of the user whose status changed
     * @param isOnline True if the user is online, false if offline
     */
    private void broadcastUserConnectionStatus(Long userId, boolean isOnline) {
        try {
            OnlineUserStatusResponseDto userStatusResponseDto = OnlineUserStatusResponseDto.builder()
                    .userId(userId)
                    .isOnline(isOnline)
                    .build();
            simpMessagingTemplate.convertAndSend("/topic/public",
                    userStatusResponseDto
            );
            log.info("Notified users about userId: {} status change to isOnline: {}", userId, isOnline);
        } catch (Exception e) {
            log.error("Error notifying users about online status: {}", e.getMessage(), e);
        }
    }
}