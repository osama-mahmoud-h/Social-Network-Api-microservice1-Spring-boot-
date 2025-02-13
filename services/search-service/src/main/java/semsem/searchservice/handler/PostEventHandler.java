package semsem.searchservice.handler;

import org.springframework.stereotype.Component;
import semsem.searchservice.dto.event.EntityEventDto;
import semsem.searchservice.enums.EntityEventType;

@Component
public class PostEventHandler implements EntityEventHandler{
    @Override
    public EntityEventType getEventType() {
        return EntityEventType.POST_CREATED;
    }

    @Override
    public void handleEvent(EntityEventDto entityEventDto) {

    }
}
