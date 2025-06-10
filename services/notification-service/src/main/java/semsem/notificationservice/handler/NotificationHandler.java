package semsem.notificationservice.handler;

import semsem.notificationservice.dto.NotificationEvent;
import semsem.notificationservice.enums.NotificationType;

public interface NotificationHandler {
    void handle(NotificationEvent event);
    NotificationType getNotificationType();
}

