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
import semsem.chatservice.security.WebSocketAuthenticationHelper;
import semsem.chatservice.service.ActiveUserService;
import semsem.chatservice.utils.OnlineUserVal;

import java.util.List;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketEventListener {
    private final ActiveUserService activeUserService;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final WebSocketAuthenticationHelper webSocketAuthenticationHelper;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        //log.info("New WebSocket connection established - sessionId: {}", headerAccessor.getSessionId());
        System.out.println("New WebSocket connection established - sessionId: "+headerAccessor.getSessionAttributes());

        String username = webSocketAuthenticationHelper.getEmail(headerAccessor);

      //  System.out.println("headerAccessor: "+headerAccessor);

        OnlineUserVal user = OnlineUserVal.builder()
                .sessionId(null)
                .username(username)
                .customUserSessionId(null)
                .build();

        //activeUserService.userConnected(null, user);

        List<OnlineUserVal> activeUsers = activeUserService.getAllActiveUsers();
        EventMessageResponseDto eventMessage = EventMessageResponseDto.builder()
                .eventType(EventMessageType.GET_ACTIVE_USERS)
                .data(activeUsers)
                .build();
        simpMessagingTemplate.convertAndSend("/topic/public", eventMessage);
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        //log.info("WebSocket connection closed - sessionId: {}", headerAccessor.getSessionId());
       // String customUserSessionId = headerAccessor.getNativeHeader("sessionId").get(0);
        activeUserService.userDisconnected(headerAccessor.getSessionId());

        System.out.println("WebSocket connection closed - sessionId: "+ headerAccessor.getSessionId()+
                ", users count: "+activeUserService.getAllActiveUsers().size());

        simpMessagingTemplate.convertAndSend("/topic/public", activeUserService.getAllActiveUsers());
    }
}