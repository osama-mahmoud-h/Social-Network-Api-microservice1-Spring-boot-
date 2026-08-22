package semsem.notificationservice.handler;

import com.app.shared.events.NotificationEvent;
import com.app.shared.events.type.NotificationType;

public interface NotificationHandler {
    void handle(NotificationEvent event);
    NotificationType getNotificationType();
}

