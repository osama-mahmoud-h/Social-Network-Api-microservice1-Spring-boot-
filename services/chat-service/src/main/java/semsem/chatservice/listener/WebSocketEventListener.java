package semsem.chatservice.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import semsem.chatservice.dto.response.OnlineUserStatusResponseDto;
import semsem.chatservice.repository.RedisOnlineUserRepository;
import semsem.chatservice.security.WebSocketAuthenticationHelper;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketEventListener {

    private static final String USER_INSTANCE_KEY_PREFIX = "user:ws:";
    private static final long   USER_INSTANCE_TTL_HOURS  = 24;

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final WebSocketAuthenticationHelper webSocketAuthenticationHelper;
    private final RedisOnlineUserRepository redisOnlineUserRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    // Unique ID for this Chat Service instance (set via env var in deployment)
    @Value("${chat.instance-id:default-instance}")
    private String instanceId;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        Long userId = webSocketAuthenticationHelper.getUserId(headerAccessor);

        if (Objects.isNull(userId)) {
            log.warn("WebSocket connect without valid userId — sessionId={}", headerAccessor.getSessionId());
            return;
        }

        redisOnlineUserRepository.addOnlineUser(userId.toString());

        // Record which Chat Service instance holds this user's WebSocket connection.
        // The Delivery Consumer uses this to route messages via Redis Pub/Sub.
        redisTemplate.opsForValue().set(
                USER_INSTANCE_KEY_PREFIX + userId,
                instanceId,
                USER_INSTANCE_TTL_HOURS, TimeUnit.HOURS
        );

        log.info("User connected: userId={}, instance={}, totalOnline={}",
                userId, instanceId, redisOnlineUserRepository.getOnlineUserCount());

        broadcastUserStatus(userId, true);
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        Long userId = webSocketAuthenticationHelper.getUserId(headerAccessor);

        if (Objects.isNull(userId)) {
            log.warn("WebSocket disconnect without valid userId — sessionId={}", headerAccessor.getSessionId());
            return;
        }

        redisOnlineUserRepository.removeOnlineUser(userId.toString());
        redisTemplate.delete(USER_INSTANCE_KEY_PREFIX + userId);

        log.info("User disconnected: userId={}, instance={}, totalOnline={}",
                userId, instanceId, redisOnlineUserRepository.getOnlineUserCount());

        broadcastUserStatus(userId, false);
    }

    private void broadcastUserStatus(Long userId, boolean isOnline) {
        try {
            simpMessagingTemplate.convertAndSend("/topic/public",
                    OnlineUserStatusResponseDto.builder().userId(userId).isOnline(isOnline).build());
        } catch (Exception e) {
            log.error("Failed to broadcast status for userId={}: {}", userId, e.getMessage(), e);
        }
    }
}