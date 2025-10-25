package semsem.chatservice.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import semsem.chatservice.service.ActiveUserService;
import semsem.chatservice.utils.OnlineUserVal;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Slf4j
@Component
public class ActiveUserServiceImpl implements ActiveUserService {
    private final ConcurrentMap<String, OnlineUserVal> activeUsers = new ConcurrentHashMap<>();

    @Override
    public void userConnected(String sessionId, OnlineUserVal user) {

        activeUsers.put(sessionId, user);
         log.info("User connected: sessionId={}, username={}", sessionId, user.getUsername());
        //   System.out.println("User connected: sessionId="+sessionId+", username="+user.getUsername());
    }

    @Override
    public void userDisconnected(String sessionId) {
        activeUsers.remove(sessionId);
         log.info("User disconnected: sessionId={}", sessionId);
        // System.out.println("User disconnected: sessionId="+sessionId);
    }

    @Override
    public List<OnlineUserVal> getAllActiveUsers() {
        log.info("Active users: {}", activeUsers.values());
        // System.out.println("Active users: "+activeUsers.values());
        return new ArrayList<>(activeUsers.values());
    }
}
