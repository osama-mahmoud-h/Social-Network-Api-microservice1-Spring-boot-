package com.app.auth.service;

import com.app.auth.event.UserCreatedEvent;
import com.app.auth.event.UserUpdatedEvent;

public interface OutboxEventService {
    void saveUserCreatedEvent(UserCreatedEvent event);
    void saveUserUpdatedEvent(UserUpdatedEvent event);
}