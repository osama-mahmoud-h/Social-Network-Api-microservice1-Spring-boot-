package semsem.searchservice.handler;

import semsem.searchservice.enums.EntityEventType;

public interface EntityEventHandler {
     EntityEventType getEventType() ;
     void handleEvent(Object entityDto);
}
