package com.app.auth.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Event published when a user is updated in auth-service
 * Consumed by main-service to sync UserProfile record
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdatedEvent {
    private Long userId;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String eventType;
}