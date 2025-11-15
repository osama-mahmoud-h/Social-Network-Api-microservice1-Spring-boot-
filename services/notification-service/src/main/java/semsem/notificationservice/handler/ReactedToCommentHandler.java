package semsem.notificationservice.handler;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import semsem.notificationservice.dto.NotificationEvent;
import semsem.notificationservice.enums.NotificationType;
import semsem.notificationservice.service.NotificationService;

@Service
@RequiredArgsConstructor
public class ReactedToCommentHandler  implements NotificationHandler {
    private static final Logger log = LoggerFactory.getLogger(ReactedToCommentHandler.class);
    private final NotificationService notificationService;

    @Override
    public void handle(NotificationEvent event) {
       log.info("Handling 'reacted to comment' notification for user: {}, from: {}",
                event.getReceiverId(), event.getSenderId());

        // Save notification to database
        notificationService.createNotification(event, event.getSenderId(), this.getNotificationType().toString());
        log.debug("Notification persisted to database");

        // TODO: Send real-time notification via WebSocket
        // TODO: Send push notification if enabled
    }

    @Override
    public NotificationType getNotificationType() {
        return NotificationType.REACTED_TO_YOUR_COMMENT;
    }
}
