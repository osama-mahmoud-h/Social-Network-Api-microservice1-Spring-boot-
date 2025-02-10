package com.app.server.dto.response.profile;


import com.app.server.dto.response.AddressResponseDto;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProfileResponseDto {
    private Long profileId;
    private String bio;
    private String imageUrl;
    private String phoneNumber;
    private String email;
    private String firstName;
    private String lastName;
    private AddressResponseDto address;
}
