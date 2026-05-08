package com.app.server.controller;


import com.app.server.controller.swagger.IProfileApi;
import com.app.server.dto.request.post.GetRecentPostsRequestDto;
import com.app.server.dto.request.profile.UpdateProfileBioRequestDto;
import com.app.server.dto.response.PostResponseDto;
import com.app.server.dto.response.profile.ProfileResponseDto;
import com.app.server.model.UserProfile;
import com.app.server.repository.UserProfileRepository;
import com.app.server.service.ProfileService;
import com.app.shared.security.dto.MyApiResponse;
import com.app.shared.security.utils.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@RestController
@RequestMapping("/api/v1/profile")
@RequiredArgsConstructor
public class ProfileController implements IProfileApi {

    private final ProfileService profileService;
    private final UserProfileRepository userProfileRepository;

    private UserProfile getCurrentUserProfile() {
        Long userId = SecurityUtils.getCurrentUserId();
        return userProfileRepository.findUserByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User profile not found for userId: " + userId));
    }

    @Override
    @PutMapping("/update/bio")
    public ResponseEntity<MyApiResponse<Boolean>> updateBio(
            @RequestBody UpdateProfileBioRequestDto bioRequestDto
    ){
        UserProfile currentUser = getCurrentUserProfile();
        boolean bioUpdated = profileService.updateBio(currentUser, bioRequestDto);
        return ResponseEntity.ok(MyApiResponse.success("Bio updated successfully", bioUpdated));
    }

    @Override
    @PutMapping(value = "/update/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MyApiResponse<Boolean>> updateImage(
            @RequestParam("image") MultipartFile image
    ){
        UserProfile currentUser = getCurrentUserProfile();
        boolean imageUpdated = profileService.updateImage(currentUser, image);
        return ResponseEntity.ok(MyApiResponse.success("Image updated successfully", imageUpdated));
    }

    @Override
    @GetMapping("/get")
    public ResponseEntity<MyApiResponse<ProfileResponseDto>> getProfile(){
        UserProfile currentUser = getCurrentUserProfile();
        ProfileResponseDto profile = profileService.getProfile(currentUser);
        return ResponseEntity.ok(MyApiResponse.success("Profile retrieved successfully", profile));
    }

    @Override
    @GetMapping("/posts")
    public ResponseEntity<MyApiResponse<?>> getMyPosts(
            @ModelAttribute @Valid GetRecentPostsRequestDto requestDto
    ){
        UserProfile currentUser = getCurrentUserProfile();
        List<PostResponseDto> posts = profileService.getMyPosts(currentUser, requestDto);
        return ResponseEntity.ok(MyApiResponse.success("Posts retrieved successfully", posts));
    }

}