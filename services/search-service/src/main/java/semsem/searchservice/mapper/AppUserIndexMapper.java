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

    public AppUserIndex AppUserEventObjectToAppUserIndex(Object appUserEvent) {
        if (appUserEvent instanceof Map) {
            Map<String, Object> appUserMap = (Map<String, Object>) appUserEvent;
            return AppUserIndex.builder()
                    .userId(Long.valueOf(appUserMap.get("userId").toString()))
                    .firstName(appUserMap.get("firstName").toString())
                    .lastName(appUserMap.get("lastName").toString())
                    .profilePictureUrl(appUserMap.get("profilePictureUrl").toString())
                    .email(appUserMap.get("email").toString())
                    .build();
        }
        return null; // or throw an exception if the input is not as expected
    }

}
