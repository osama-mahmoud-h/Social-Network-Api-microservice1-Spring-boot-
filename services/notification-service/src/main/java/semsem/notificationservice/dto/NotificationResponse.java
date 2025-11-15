package semsem.notificationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import semsem.notificationservice.enums.NotificationType;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {
    private Long id;
    private NotificationType type;
    private String message;
    private Long senderId;
    private Long receiverId;
    private Boolean isRead;
    private LocalDateTime readAt;
    private LocalDateTime createdAt;
    private Long referenceId;
    private String referenceType;
}