package com.example.server.payload.response;

import com.example.server.models.SkillTracker;
import com.example.server.models.User;
import lombok.Data;
import lombok.ToString;

import java.util.Set;

@Data
@ToString
public class ProfileResponseDto {

    private Long id;

    private User user;

    Set<SkillTracker> skills;

    private String education;

    private String aboutUser;

    private String bio;

    private String image_url;

}
