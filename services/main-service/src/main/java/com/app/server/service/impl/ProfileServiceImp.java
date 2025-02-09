package com.app.server.service.impl;

import com.app.server.dto.request.profile.UpdateProfileBioRequestDto;
import com.app.server.dto.response.PostResponseDto;
import com.app.server.dto.response.ProfileResponseDto;
import com.app.server.model.AppUser;
import com.app.server.model.Profile;
import com.app.server.repository.ProfileRepository;
import com.app.server.repository.AppUserRepository;
import com.app.server.service.ProfileService;
import com.app.server.service.UserService;
import com.app.server.utils.fileStorage.FilesStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class ProfileServiceImp implements ProfileService {
    private final ProfileRepository profileRepository;
    private final UserService userService;
    private final FilesStorageService filesStorageService;
    private final AppUserRepository appUserRepository;

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

    /**
     *TODO: Implement this method(get my reactions on each post), use pagination
     * TODO: count the number of reactions on each post
     * TODO: count the number of comments on each post
     */
    @Override
    public Set<PostResponseDto> getMyPosts(AppUser appUser) {
        return null;
    }

    /**
     * TODO: enhance profile response dto form
     */
    @Override
    public ProfileResponseDto getProfile(AppUser appUser) {
        Profile profile = profileRepository.findByUserId(appUser.getUserId())
                .orElseThrow(() -> new RuntimeException("Profile not found"));
        return ProfileResponseDto.builder()
                .aboutUser(profile.getAboutUser())
                .bio(profile.getBio())
                .imageUrl(profile.getImageUrl())
                .build();
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
