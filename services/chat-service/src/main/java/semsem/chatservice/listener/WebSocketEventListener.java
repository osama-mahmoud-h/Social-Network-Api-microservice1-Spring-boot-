package semsem.chatservice.listener;




import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import semsem.chatservice.dto.response.EventMessageResponseDto;
import semsem.chatservice.enums.EventMessageType;
import semsem.chatservice.repository.RedisOnlineUserRepository;
import semsem.chatservice.security.WebSocketAuthenticationHelper;
import semsem.chatservice.service.ActiveUserService;
import semsem.chatservice.utils.OnlineUserVal;

import java.util.List;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketEventListener {
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final WebSocketAuthenticationHelper webSocketAuthenticationHelper;
    private final RedisOnlineUserRepository redisOnlineUserRepository;
    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        //log.info("New WebSocket connection established - sessionId: {}", headerAccessor.getSessionId());
        System.out.println("New WebSocket connection established - sessionId: "+headerAccessor.getSessionAttributes());
        Long userId = webSocketAuthenticationHelper.getUserId(headerAccessor);

        if (Objects.nonNull(userId)) {
          redisOnlineUserRepository.addOnlineUser(userId.toString());
          long onlineUsersCount = redisOnlineUserRepository.getOnlineUserCount();

            System.out.println("User connected - userId: " + userId +
                    ", sessionId: " + headerAccessor.getSessionId() +
                    ", total online users: " + onlineUsersCount);

            simpMessagingTemplate.convertAndSend("/topic/public", redisOnlineUserRepository.getAllOnlineUsers());
        } else {
            System.out.println("WebSocket connection established without valid user info - sessionId: " + headerAccessor.getSessionId());
        }

    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        Long userId = webSocketAuthenticationHelper.getUserId(headerAccessor);
        if (Objects.nonNull(userId)) {
            redisOnlineUserRepository.removeOnlineUser(userId.toString());
            long onlineUsersCount = redisOnlineUserRepository.getOnlineUserCount();

            System.out.println("User disconnected - userId: " + userId +
                    ", sessionId: " + headerAccessor.getSessionId() +
                    ", total online users: " + onlineUsersCount);

            simpMessagingTemplate.convertAndSend("/topic/public", redisOnlineUserRepository.getAllOnlineUsers());
        } else {
            System.out.println("WebSocket disconnection without valid user info - sessionId: " + headerAccessor.getSessionId());
        }
    }
}