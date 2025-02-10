package com.app.server.service;


import com.app.server.dto.request.post.GetRecentPostsRequestDto;
import com.app.server.dto.request.profile.UpdateProfileBioRequestDto;
import com.app.server.dto.response.PostResponseDto;
import com.app.server.dto.response.profile.ProfileResponseDto;
import com.app.server.model.AppUser;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

public interface ProfileService {


    //update profile bio
    boolean updateBio(AppUser appUser, UpdateProfileBioRequestDto requestDto);

    //update profile image
    boolean updateImage(AppUser appUser, MultipartFile file);


    Set<PostResponseDto> getMyPosts(AppUser appUser, GetRecentPostsRequestDto requestDto);

    ProfileResponseDto getProfile(AppUser appUser);
}
