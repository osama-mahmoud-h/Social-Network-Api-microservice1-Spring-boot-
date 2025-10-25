package semsem.notificationservice.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import semsem.notificationservice.dto.NotificationEvent;
import semsem.notificationservice.enums.NotificationType;

@Service
public class ReactedToPostHandler implements NotificationHandler {
    private static final Logger log = LoggerFactory.getLogger(ReactedToPostHandler.class);
    @Override
    public void handle(NotificationEvent event) {
        log.info("Handling 'reacted to post' notification for user: {}, from: {}",
                 event.getReceiverId(), event.getSenderId());
        // TODO: Save notification to database
        // TODO: Send real-time notification via WebSocket
        // TODO: Send push notification if enabled
    }

    @Override
    public NotificationType getNotificationType() {
        return NotificationType.REACTED_TO_YOUR_POST;
    }
}
