package semsem.searchservice.mapper;

import org.springframework.stereotype.Component;
import semsem.searchservice.dto.response.AppUserResponseDto;
import semsem.searchservice.model.AppUserIndex;

import java.util.Map;

@Component
public class AppUserIndexMapper {
    public Object mapAppUserToAppUserIndex(Object appUser) {
        return null;
    }

    public AppUserResponseDto mapAppUserIndexToAppUserResponseDto(AppUserIndex user){
        return AppUserResponseDto.builder()
                .userId(Long.valueOf(user.getUserId()))
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .profilePictureUrl(user.getProfilePictureUrl())
                .email(user.getEmail())
                .build();
    }

    public AppUserIndex toAppUserIndex(Map<String, Object> event) {
        Long userId = Long.valueOf(event.get("user_id").toString());
        Object profilePic = event.get("profile_picture_url");
        return AppUserIndex.builder()
                .id(userId.toString()) // deterministic ES doc ID → upsert is idempotent
                .userId(userId)
                .firstName(event.get("first_name") != null ? event.get("first_name").toString() : null)
                .lastName(event.get("last_name") != null ? event.get("last_name").toString() : null)
                .email(event.get("email") != null ? event.get("email").toString() : null)
                .profilePictureUrl(profilePic != null ? profilePic.toString() : null)
                .build();
    }

}
