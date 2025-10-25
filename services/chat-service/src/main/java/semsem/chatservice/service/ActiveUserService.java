package semsem.chatservice.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import semsem.chatservice.utils.OnlineUserVal;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


public interface ActiveUserService {

     void userConnected(String sessionId, OnlineUserVal user);

     void userDisconnected(String sessionId) ;

     List<OnlineUserVal> getAllActiveUsers();
}

