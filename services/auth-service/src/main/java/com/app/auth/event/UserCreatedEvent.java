package com.app.auth.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Event published when a new user is created in auth-service
 * Consumed by main-service to create UserProfile record
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCreatedEvent {
    private Long userId;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private Instant createdAt;
    private String eventType;
}