package semsem.searchservice.handler;

import org.springframework.stereotype.Component;
import semsem.searchservice.dto.event.EntityEventDto;
import semsem.searchservice.enums.EntityEventType;

@Component
public class CommentEventHandler implements EntityEventHandler{
    @Override
    public EntityEventType getEventType() {
        return EntityEventType.COMMENT_CREATED;
    }

    @Override
    public void handleEvent(EntityEventDto entityEventDto) {

    }
}
