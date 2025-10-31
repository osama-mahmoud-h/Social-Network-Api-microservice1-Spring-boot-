package com.app.server.mapper;

import com.app.server.dto.response.AppUserResponseDto;
import com.app.server.projection.UserSuggestionProjection;
import org.springframework.stereotype.Component;

/**
 * Mapper to convert UserSuggestionProjection to DTOs
 * Provides type-safe mapping with full IDE support
 */
@Component
public class UserProjectionMapper {

    /**
     * Convert UserSuggestionProjection to AppUserResponseDto
     * Type-safe - IDE will autocomplete projection methods!
     */
    public AppUserResponseDto toAppUserResponseDto(UserSuggestionProjection projection) {
        return new AppUserResponseDto(
                projection.getUserId(),
                projection.getFirstName() + " " + projection.getLastName(),
                projection.getEmail(),
                projection.getProfilePictureUrl()
        );
    }
}