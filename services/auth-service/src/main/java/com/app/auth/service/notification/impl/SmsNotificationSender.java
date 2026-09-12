package com.app.auth.service.notification.impl;

import com.app.auth.model.enums.NotificationChannel;
import com.app.auth.service.notification.NotificationSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
public class SmsNotificationSender implements NotificationSender {

    @Override
    public boolean supports(NotificationChannel channel) {
        return channel == NotificationChannel.SMS;
    }

    @Override
    public void sendTemplateNotification(String destination, String subject, String templateName, Map<String, Object> variables) {
        // Since SMS doesn't typically use HTML templates, we just format a plain text string
        String code = (String) variables.get("otpCode");
        String purpose = (String) variables.get("purpose");
        String message = String.format("SocialNetwork: Your %s OTP is %s. Expires in 10m.", purpose, code);
        
        sendNotification(destination, subject, message);
    }

    @Override
    public void sendNotification(String destination, String subject, String messageContent) {
        // TODO: Integrate actual Twilio / AWS SNS logic here in the future
        log.info(">>>> [MOCK SMS] Sending SMS to [{}]. Content: '{}'", destination, messageContent);
    }
}
