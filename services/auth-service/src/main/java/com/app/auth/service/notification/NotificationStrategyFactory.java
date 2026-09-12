package com.app.auth.service.notification;

import com.app.auth.model.enums.NotificationChannel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationStrategyFactory {

    private final List<NotificationSender> senders;

    public NotificationSender getSender(NotificationChannel channel) {
        return senders.stream()
                .filter(sender -> sender.supports(channel))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported notification channel: " + channel));
    }
}
