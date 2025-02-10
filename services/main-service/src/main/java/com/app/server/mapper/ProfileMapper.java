package com.app.server.mapper;

import com.app.server.dto.response.profile.ProfileResponseDto;
import com.app.server.model.AppUser;
import com.app.server.model.Profile;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileMapper {
    private final AddressMapper addressMapper;

    public ProfileResponseDto mapToProfileResponseDto(Profile profile) {
        return ProfileResponseDto.builder()
                .profileId(profile.getProfileId())
                .bio(profile.getBio())
                .imageUrl(profile.getImageUrl())
                .phoneNumber(profile.getUser().getPhoneNumber())
                .email(profile.getUser().getEmail())
                .firstName(profile.getUser().getFirstName())
                .lastName(profile.getUser().getLastName())
                .address(addressMapper.mapToAddressResponseDto(profile.getAddress()))
                .build();
    }
}
