package semsem.chatservice.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import semsem.chatservice.dto.response.EventMessageResponseDto;
import semsem.chatservice.enums.EventMessageType;
import semsem.chatservice.model.AppUser;
import semsem.chatservice.repository.RedisOnlineUserRepository;
import semsem.chatservice.service.UserService;


import java.util.List;
import java.util.Set;

@Slf4j
@Controller
@RequiredArgsConstructor
public class AppUserController {
    private final UserService userService;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final RedisOnlineUserRepository redisOnlineUserRepository;

    @MessageMapping("/user.addUser")
    @SendTo("/topic/public")
    public AppUser addUser(@Payload AppUser user) {
        log.info("Adding new  user: {}", user);
        userService.saveUser(user);
        return user;
    }

    @MessageMapping("/user.disconnectUser")
    @SendTo("/topic/public")
    public AppUser disconnect(@Payload AppUser user) {
        userService.disconnect(user);
        return user;
    }

    @GetMapping("/users")
    public ResponseEntity<List<AppUser>> getConnectedUsers() {
        return ResponseEntity.ok(userService.getConnectedUsers());
    }

    /**
     * REST endpoint to get online user IDs from Redis
     * GET /api/users/online
     */
    @GetMapping("/api/users/online")
    public ResponseEntity<Set<String>> getOnlineUsers() {
        Set<String> onlineUsers = redisOnlineUserRepository.getAllOnlineUsers();
        return ResponseEntity.ok(onlineUsers);
    }

    /**
     * REST endpoint to check if a user is online
     * GET /api/users/{userId}/online
     */
    @GetMapping("/api/users/{userId}/online")
    public ResponseEntity<Boolean> isUserOnline(@org.springframework.web.bind.annotation.PathVariable String userId) {
        boolean isOnline = redisOnlineUserRepository.isUserOnline(userId);
        return ResponseEntity.ok(isOnline);
    }

    @MessageMapping("/getActiveUsers")
    public void getActiveUsers() {
        List<AppUser> activeUsers = userService.getConnectedUsers();
        EventMessageResponseDto eventMessage = EventMessageResponseDto.builder()
                .eventType(EventMessageType.GET_ACTIVE_USERS)
                .data(activeUsers)
                .build();
        System.out.println("Active users: " + activeUsers);
        simpMessagingTemplate.convertAndSend("/topic/public", eventMessage);
    }



}
