package semsem.notificationservice.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import semsem.notificationservice.enums.EventType;
import semsem.notificationservice.enums.NotificationType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationEvent implements DomainEvent {
    private NotificationType type;
    private String message;
    private Long senderId;
    private Long receiverId;
   // private Object payload;

    @Override
    public String getEventType() {
        return EventType.NOTIFICATION_EVENT.name();
    }

    @Override
    public String getActionType() {
        return type != null ? type.name() : "UNKNOWN";
    }
}
