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
import org.springframework.web.bind.annotation.PathVariable;
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
//    private final UserService userService;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final RedisOnlineUserRepository redisOnlineUserRepository;


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
    public ResponseEntity<Boolean> isUserOnline(@PathVariable String userId) {
        boolean isOnline = redisOnlineUserRepository.isUserOnline(userId);
        return ResponseEntity.ok(isOnline);
    }



}
