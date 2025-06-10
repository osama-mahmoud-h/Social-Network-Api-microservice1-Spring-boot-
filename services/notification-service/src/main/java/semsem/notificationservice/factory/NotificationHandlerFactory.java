package semsem.notificationservice.factory;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import semsem.notificationservice.enums.NotificationType;
import semsem.notificationservice.handler.NotificationHandler;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationHandlerFactory {

    private final Map<NotificationType, NotificationHandler> handlerMap;

    @Autowired
    public NotificationHandlerFactory(List<NotificationHandler> handlers) {
        handlerMap = handlers.stream()
                .collect(Collectors.toMap(NotificationHandler::getNotificationType, Function.identity()));
        System.out.println("handlerMap = " + handlerMap);
    }

    public NotificationHandler getHandler(NotificationType type) {
        return Optional.ofNullable(handlerMap.get(type))
                .orElseThrow(() -> new IllegalArgumentException("No handler found for type: " + type));
    }
}
