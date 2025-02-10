package com.app.server.service.impl;

import com.app.server.dto.request.post.GetRecentPostsRequestDto;
import com.app.server.dto.request.profile.UpdateProfileBioRequestDto;
import com.app.server.dto.response.PostResponseDto;
import com.app.server.dto.response.profile.ProfileResponseDto;
import com.app.server.exception.CustomRuntimeException;
import com.app.server.mapper.ProfileMapper;
import com.app.server.model.AppUser;
import com.app.server.model.Profile;
import com.app.server.repository.PostRepository;
import com.app.server.repository.ProfileRepository;
import com.app.server.repository.AppUserRepository;
import com.app.server.service.PostService;
import com.app.server.service.ProfileService;
import com.app.server.service.UserService;
import com.app.server.utils.fileStorage.FilesStorageService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class ProfileServiceImp implements ProfileService {
    private static final Logger log = LoggerFactory.getLogger(ProfileServiceImp.class);
    private final ProfileRepository profileRepository;
    private final UserService userService;
    private final FilesStorageService filesStorageService;
    private final AppUserRepository appUserRepository;
    private final PostRepository postRepository;
    private final PostService postService;
    private final ProfileMapper profileMapper;

    //update profile bio
    @Override
    public boolean updateBio(AppUser appUser, UpdateProfileBioRequestDto requestDto) {
        Profile profile = profileRepository.findByUserId(appUser.getUserId())
                .orElseThrow(() -> new RuntimeException("Profile not found"));
        profileRepository.updateBio(profile.getProfileId(), requestDto.getBio());
        return true;
    }

    //update profile image
    @Override
    public boolean updateImage(AppUser appUser, MultipartFile file) {
        Profile profile = profileRepository.findByUserId(appUser.getUserId())
                .orElseThrow(() -> new RuntimeException("Profile not found"));
        this.deleteOldImage(profile);
        this.uploadProfileImage(file);
        profileRepository.updateImage(profile.getProfileId(), filesStorageService.save(file));
        return true;
    }

    @Override
    public Set<PostResponseDto> getMyPosts(AppUser appUser, GetRecentPostsRequestDto requestDto) {
        return this.postService.getRecentPosts(appUser, requestDto);
    }

    @Override
    public ProfileResponseDto getProfile(AppUser appUser) {
        log.info("Getting profile for user: {}", appUser.getUsername());
        Profile profile = profileRepository.findByUserId(appUser.getUserId())
                .orElseThrow(() -> new CustomRuntimeException("Profile not found", HttpStatus.NOT_FOUND));
        return profileMapper.mapToProfileResponseDto(profile);
    }



    private boolean deleteOldImage(Profile profile) {
        if (profile.getImageUrl() != null) {
            filesStorageService.deleteFile(profile.getImageUrl());
        }
        return true;
    }

    private String uploadProfileImage(MultipartFile file) {
        return filesStorageService.save(file);
    }


}
