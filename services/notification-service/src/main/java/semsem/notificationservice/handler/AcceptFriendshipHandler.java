package semsem.notificationservice.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import semsem.notificationservice.dto.NotificationEvent;
import semsem.notificationservice.enums.NotificationType;

@Service
public class AcceptFriendshipHandler  implements NotificationHandler {
    private static final Logger log = LoggerFactory.getLogger(AcceptFriendshipHandler.class);
    @Override
    public void handle(NotificationEvent event) {
        log.info("Handling friendship request for user: {} ", event.getReceiverId());
    }

    @Override
    public NotificationType getNotificationType() {
        return NotificationType.ACCEPT_FRIENDSHIP;
    }
}
