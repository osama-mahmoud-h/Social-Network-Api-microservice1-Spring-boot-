package semsem.chatservice.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

@Slf4j
@Component
public class SubscriptionEventListener {

    @EventListener
    public void handleSubscribeEvent(SessionSubscribeEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

//        log.info("New subscription - destination: {}, sessionId: {}",
//                headerAccessor.getDestination(),
//                headerAccessor.getSessionId());
//        System.out.println("New subscription - destination: "+headerAccessor.getDestination()+
//                ", sessionId: "+headerAccessor.getSessionId());
    }
}
