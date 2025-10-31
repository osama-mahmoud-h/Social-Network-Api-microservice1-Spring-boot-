package com.app.server.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

/**
 * UserProfile - Minimal user data synchronized from auth-service
 * This entity stores only the user information needed for relationships in main-service
 * The auth-service is the source of truth for user authentication and credentials
 */
@Entity
@Table(name = "user_profiles")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfile {

    /**
     * User ID from auth-service (same ID)
     */
    @Id
    @Column(name = "user_id")
    private Long userId;

    @Column(nullable = false)
    private String firstName;

    private String lastName;

    @Column(unique = true, nullable = false)
    private String email;

    private String phoneNumber;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column
    private Instant syncedAt; // Last time synced with auth-service

    // NO password field - that's in auth-service only
    // NO roles field - that's in auth-service only
}