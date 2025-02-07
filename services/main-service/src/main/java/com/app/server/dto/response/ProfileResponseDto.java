package com.app.server.dto.response;

import com.app.server.model.AppUser;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ProfileResponseDto {

    private Long id;

    private AppUser user;

   // Set<SkillTracker> skills;

    private String education;

    private String aboutUser;

    private String bio;

    private String image_url;

}
