package com.example.server.payload.response.profile;

import com.example.server.models.Post;
import com.example.server.models.SkillTracker;
import com.example.server.models.User;
import com.example.server.payload.request.profile.ContactInfoDto;
import com.example.server.payload.request.profile.EducationRequestDto;
import com.example.server.payload.response.PostResponceDto;
import com.example.server.payload.response.UserResponceDto;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Setter
@Getter
@ToString
public class ProfileResponseDto {

    private Long id;

    private UserResponceDto user;

    private String bio;

    private String image_url;

    private String coverImage_url;

    private List<PostResponceDto> userPosts;

    private EducationRequestDto education;

    private ContactInfoDto contactInfo;

    private Set<String>skills;

    private Map<String,String> socialLinks;

}
