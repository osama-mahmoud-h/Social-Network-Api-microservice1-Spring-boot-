package com.app.server.dto.notification;


import com.app.server.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationEvent implements Serializable {
    private NotificationType type;
    private String message;
    private Long senderId;
    private Long receiverId;
  //  private Object payload;
}
