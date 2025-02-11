package semsem.notificationservice.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import semsem.notificationservice.enums.NotificationType;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationEvent implements Serializable {
    private NotificationType type;
    private String message;
    private Long senderId;
    private Long receiverId;
   // private Object payload;

}
