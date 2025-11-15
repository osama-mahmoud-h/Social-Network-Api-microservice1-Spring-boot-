package semsem.notificationservice.mapper;

import semsem.notificationservice.dto.NotificationEvent;
import semsem.notificationservice.dto.NotificationResponse;
import semsem.notificationservice.model.Notification;

public class NotificationMapper {

    /**
     * Convert Notification entity to NotificationResponse DTO
     */
    public static NotificationResponse toResponse(Notification notification) {
        if (notification == null) {
            return null;
        }

        return NotificationResponse.builder()
                .id(notification.getId())
                .type(notification.getType())
                .message(notification.getMessage())
                .senderId(notification.getSenderId())
                .receiverId(notification.getReceiverId())
                .isRead(notification.getIsRead())
                .readAt(notification.getReadAt())
                .createdAt(notification.getCreatedAt())
                .referenceId(notification.getReferenceId())
                .referenceType(notification.getReferenceType())
                .build();
    }

    /**
     * Convert NotificationEvent to Notification entity
     */
    public static Notification toEntity(NotificationEvent event) {
        if (event == null) {
            return null;
        }

        return Notification.builder()
                .type(event.getType())
                .message(event.getMessage())
                .senderId(event.getSenderId())
                .receiverId(event.getReceiverId())
                .isRead(false)
                .build();
    }

    /**
     * Convert NotificationEvent to Notification entity with reference
     */
    public static Notification toEntity(NotificationEvent event, Long referenceId, String referenceType) {
        Notification notification = toEntity(event);
        if (notification != null) {
            notification.setReferenceId(referenceId);
            notification.setReferenceType(referenceType);
        }
        return notification;
    }
}