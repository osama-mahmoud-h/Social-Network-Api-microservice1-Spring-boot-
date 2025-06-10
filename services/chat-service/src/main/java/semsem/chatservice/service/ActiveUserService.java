package semsem.chatservice.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import semsem.chatservice.utils.OnlineUserVal;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Slf4j
@Component
public class ActiveUserService {

    private final ConcurrentMap<String, OnlineUserVal> activeUsers = new ConcurrentHashMap<>();

    public void userConnected(String sessionId, OnlineUserVal user) {

        activeUsers.put(sessionId, user);
       // log.info("User connected: sessionId={}, username={}", sessionId, username);
     //   System.out.println("User connected: sessionId="+sessionId+", username="+user.getUsername());
    }

    public void userDisconnected(String sessionId) {
        activeUsers.remove(sessionId);
       // log.info("User disconnected: sessionId={}", sessionId);
       // System.out.println("User disconnected: sessionId="+sessionId);
    }

    public List<OnlineUserVal> getAllActiveUsers() {
        //log.info("Active users: {}", activeUsers.values());
       // System.out.println("Active users: "+activeUsers.values());
        return new ArrayList<>(activeUsers.values());
    }
}

