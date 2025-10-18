package com.app.server.controller;


import com.app.server.dto.request.post.GetRecentPostsRequestDto;
import com.app.server.dto.request.profile.UpdateProfileBioRequestDto;
import com.app.server.dto.response.MyApiResponse;
import com.app.server.dto.response.PostResponseDto;
import com.app.server.dto.response.profile.ProfileResponseDto;
import com.app.server.model.UserProfile;
import com.app.server.service.ProfileService;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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

    @Operation(summary = "Update profile bio", description = "Update user's profile biography")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Bio updated successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PutMapping("/update/bio")
    public ResponseEntity<MyApiResponse<Boolean>> updateBio(
            @AuthenticationPrincipal UserDetails currentUserDetails,
            @RequestBody UpdateProfileBioRequestDto bioRequestDto
    ){
        boolean bioUpdated = profileService.updateBio((UserProfile) currentUserDetails, bioRequestDto);
        return ResponseEntity.ok(MyApiResponse.success(bioUpdated, "Bio updated successfully"));
    }

    @Operation(summary = "Update profile image", description = "Update user's profile picture")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Image updated successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PutMapping(value = "/update/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MyApiResponse<Boolean>> updateImage(
            @AuthenticationPrincipal UserDetails currentUserDetails,
            @RequestParam("image") MultipartFile image
    ){
        boolean imageUpdated = profileService.updateImage((UserProfile) currentUserDetails, image);
        return ResponseEntity.ok(MyApiResponse.success(imageUpdated, "Image updated successfully"));
    }

    @Operation(summary = "Get profile", description = "Retrieve current user's profile information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Profile retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/get")
    public ResponseEntity<MyApiResponse<ProfileResponseDto>> getProfile(@AuthenticationPrincipal UserDetails currentUserDetails){
        ProfileResponseDto profile = profileService.getProfile((UserProfile) currentUserDetails);
        return ResponseEntity.ok(MyApiResponse.success(profile, "Profile get successfully"));
    }

    @Operation(summary = "Get user's posts", description = "Retrieve all posts created by the current user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Posts retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/posts")
    public ResponseEntity<MyApiResponse<?>> getMyPosts(
            @AuthenticationPrincipal UserDetails currentUserDetails,
            @ModelAttribute @Valid GetRecentPostsRequestDto requestDto
    ){
        Set<PostResponseDto> posts = profileService.getMyPosts((UserProfile) currentUserDetails, requestDto);
        return ResponseEntity.ok(MyApiResponse.success(posts, "Posts get successfully"));
    }

}
