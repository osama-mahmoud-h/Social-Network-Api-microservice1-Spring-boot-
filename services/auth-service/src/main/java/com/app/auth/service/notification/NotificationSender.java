package com.app.auth.service.notification;

import com.app.auth.model.enums.NotificationChannel;
import java.util.Map;

public interface NotificationSender {
    boolean supports(NotificationChannel channel);
    
    void sendNotification(String destination, String subject, String messageContent);
    
    void sendTemplateNotification(String destination, String subject, String templateName, Map<String, Object> variables);
}
