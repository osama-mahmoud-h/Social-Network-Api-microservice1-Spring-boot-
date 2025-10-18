package com.app.server.service;


import com.app.server.dto.request.post.GetRecentPostsRequestDto;
import com.app.server.dto.request.profile.UpdateProfileBioRequestDto;
import com.app.server.dto.response.PostResponseDto;
import com.app.server.dto.response.profile.ProfileResponseDto;
import com.app.server.model.UserProfile;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

public interface ProfileService {


    //update profile bio
    boolean updateBio(UserProfile userProfile, UpdateProfileBioRequestDto requestDto);

    //update profile image
    boolean updateImage(UserProfile userProfile, MultipartFile file);


    Set<PostResponseDto> getMyPosts(UserProfile userProfile, GetRecentPostsRequestDto requestDto);

    ProfileResponseDto getProfile(UserProfile userProfile);
}
