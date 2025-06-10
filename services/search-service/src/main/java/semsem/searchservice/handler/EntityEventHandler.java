package semsem.searchservice.handler;

import semsem.searchservice.dto.event.EntityEventDto;
import semsem.searchservice.enums.EntityEventType;

public interface EntityEventHandler {
     EntityEventType getEventType() ;
     void handleEvent(Object entityDto);
}
