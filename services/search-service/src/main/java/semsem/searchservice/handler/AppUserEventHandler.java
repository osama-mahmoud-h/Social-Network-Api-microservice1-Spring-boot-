package semsem.searchservice.handler;

import org.springframework.stereotype.Component;
import semsem.searchservice.dto.event.AppUserEventDto;
import semsem.searchservice.dto.event.EntityEventDto;
import semsem.searchservice.enums.EntityEventType;

@Component
public class AppUserEventHandler implements EntityEventHandler{
    @Override
    public EntityEventType getEventType() {
        return EntityEventType.APP_USER_CREATED;
    }

    @Override
    public void handleEvent(EntityEventDto entityEventDto) {

    }
}
