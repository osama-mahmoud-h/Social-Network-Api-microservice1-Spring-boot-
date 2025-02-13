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

    public AppUserResponseDto mapDbObjectIndexToAppUserResponseDto(Map<String,Object> hitMap) {
        return AppUserResponseDto.builder()
                .userId(Long.valueOf(hitMap.get("userId").toString()))
                .firstName(hitMap.get("firstName").toString())
                .lastName(hitMap.get("lastName").toString())
                .profilePictureUrl(hitMap.get("profilePictureUrl").toString())
                .email(hitMap.get("email").toString())
                .build();
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

}
