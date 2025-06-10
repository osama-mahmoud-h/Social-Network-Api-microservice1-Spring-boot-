package semsem.searchservice.factory;

import org.springframework.stereotype.Component;
import semsem.searchservice.enums.EntityEventType;
import semsem.searchservice.enums.IndexType;
import semsem.searchservice.handler.EntityEventHandler;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class EntityEventHandlerFactory {
    private final Map<EntityEventType, EntityEventHandler> entityHandlerMap;

    public EntityEventHandlerFactory(List<EntityEventHandler> entityHandlers) {
        this.entityHandlerMap = entityHandlers.stream()
                .collect(Collectors.toMap(EntityEventHandler::getEventType, Function.identity()));
    }
    public EntityEventHandler getEntityEventHandler(EntityEventType eventType) {
         return Optional.ofNullable(entityHandlerMap.get(eventType)).
                 orElseThrow(() -> new IllegalArgumentException("No handler found for event type: " + eventType));
    }
}
