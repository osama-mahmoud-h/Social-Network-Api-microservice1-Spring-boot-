package com.app.server.projection;

/**
 * Projection interface for user suggestions and friend queries
 * Spring Data JPA will automatically map query results to these getters
 */
public interface UserSuggestionProjection {
    Long getUserId();
    String getEmail();
    String getFirstName();
    String getLastName();
    String getProfilePictureUrl();
}