package com.app.auth.service.notification.impl;

import com.app.auth.model.enums.NotificationChannel;
import com.app.auth.service.notification.NotificationSender;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailNotificationSender implements NotificationSender {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Override
    public boolean supports(NotificationChannel channel) {
        return channel == NotificationChannel.EMAIL;
    }

    @Override
    public void sendTemplateNotification(String destination, String subject, String templateName, Map<String, Object> variables) {
        Context context = new Context();
        context.setVariables(variables);
        String htmlContent = templateEngine.process(templateName, context);
        
        sendNotification(destination, subject, htmlContent);
    }

    @Override
    public void sendNotification(String destination, String subject, String messageContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(destination);
            helper.setSubject(subject);
            helper.setText(messageContent, true);

            mailSender.send(message);
            log.info("Email [{}] sent successfully to: {}", subject, destination);
        } catch (MessagingException e) {
            log.error("Failed to send email to: {}", destination, e);
            throw new RuntimeException("Failed to send email", e);
        }
    }
}
