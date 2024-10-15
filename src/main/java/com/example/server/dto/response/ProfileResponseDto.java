package com.example.server.dto.response;

import com.example.server.model.AppUser;
import com.example.server.model.SkillTracker;
import lombok.Data;
import lombok.ToString;

import java.util.Set;

@Data
@ToString
public class ProfileResponseDto {

    private Long id;

    private AppUser user;

    Set<SkillTracker> skills;

    private String education;

    private String aboutUser;

    private String bio;

    private String image_url;

}
