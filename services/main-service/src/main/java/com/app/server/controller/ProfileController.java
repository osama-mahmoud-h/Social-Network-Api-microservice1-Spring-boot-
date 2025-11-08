package com.app.server.controller;


import com.app.server.dto.request.post.GetRecentPostsRequestDto;
import com.app.server.dto.request.profile.UpdateProfileBioRequestDto;
import com.app.shared.security.dto.MyApiResponse;
import com.app.server.dto.response.PostResponseDto;
import com.app.server.dto.response.profile.ProfileResponseDto;
import com.app.server.model.UserProfile;
import com.app.server.repository.UserProfileRepository;
import com.app.server.service.ProfileService;
import com.app.shared.security.utils.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;


@RestController
@RequestMapping("/api/v1/profile")
@RequiredArgsConstructor
@Tag(name = "Profile", description = "APIs for managing user profiles")
@SecurityRequirement(name = "jwtAuth")
public class ProfileController {

    private final ProfileService profileService;
    private final UserProfileRepository userProfileRepository;

    private UserProfile getCurrentUserProfile() {
        Long userId = SecurityUtils.getCurrentUserId();
        return userProfileRepository.findUserByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User profile not found for userId: " + userId));
    }

    @Operation(summary = "Update profile bio", description = "Update user's profile biography")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Bio updated successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PutMapping("/update/bio")
    public ResponseEntity<MyApiResponse<Boolean>> updateBio(
            @RequestBody UpdateProfileBioRequestDto bioRequestDto
    ){
        UserProfile currentUser = getCurrentUserProfile();
        boolean bioUpdated = profileService.updateBio(currentUser, bioRequestDto);
        return ResponseEntity.ok(MyApiResponse.success("Bio updated successfully", bioUpdated));
    }

    @Operation(summary = "Update profile image", description = "Update user's profile picture")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Image updated successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PutMapping(value = "/update/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MyApiResponse<Boolean>> updateImage(
            @RequestParam("image") MultipartFile image
    ){
        UserProfile currentUser = getCurrentUserProfile();
        boolean imageUpdated = profileService.updateImage(currentUser, image);
        return ResponseEntity.ok(MyApiResponse.success("Image updated successfully", imageUpdated));
    }

    @Operation(summary = "Get profile", description = "Retrieve current user's profile information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Profile retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/get")
    public ResponseEntity<MyApiResponse<ProfileResponseDto>> getProfile(){
        UserProfile currentUser = getCurrentUserProfile();
        ProfileResponseDto profile = profileService.getProfile(currentUser);
        return ResponseEntity.ok(MyApiResponse.success("Profile retrieved successfully", profile));
    }

    @Operation(summary = "Get user's posts", description = "Retrieve all posts created by the current user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Posts retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/posts")
    public ResponseEntity<MyApiResponse<?>> getMyPosts(
            @ModelAttribute @Valid GetRecentPostsRequestDto requestDto
    ){
        UserProfile currentUser = getCurrentUserProfile();
        Set<PostResponseDto> posts = profileService.getMyPosts(currentUser, requestDto);
        return ResponseEntity.ok(MyApiResponse.success("Posts retrieved successfully", posts));
    }

}