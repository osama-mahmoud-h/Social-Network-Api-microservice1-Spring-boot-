package com.app.server.controller;


import com.app.server.dto.request.post.GetRecentPostsRequestDto;
import com.app.server.dto.request.profile.UpdateProfileBioRequestDto;
import com.app.server.dto.response.MyApiResponse;
import com.app.server.dto.response.PostResponseDto;
import com.app.server.dto.response.profile.ProfileResponseDto;
import com.app.server.model.AppUser;
import com.app.server.service.ProfileService;
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
public class ProfileController {

    private final ProfileService profileService;

    //update profile bio
    @PutMapping("/update/bio")
    public ResponseEntity<MyApiResponse<Boolean>> updateBio(
            @AuthenticationPrincipal UserDetails currentUserDetails,
            @RequestBody UpdateProfileBioRequestDto bioRequestDto
    ){
        boolean bioUpdated = profileService.updateBio((AppUser) currentUserDetails, bioRequestDto);
        return ResponseEntity.ok(MyApiResponse.success(bioUpdated, "Bio updated successfully"));
    }

    //update profile image
    @PutMapping(value = "/update/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MyApiResponse<Boolean>> updateImage(
            @AuthenticationPrincipal UserDetails currentUserDetails,
            @RequestParam("image") MultipartFile image
    ){
        boolean imageUpdated = profileService.updateImage((AppUser) currentUserDetails, image);
        return ResponseEntity.ok(MyApiResponse.success(imageUpdated, "Image updated successfully"));
    }

    @GetMapping("/get")
    public ResponseEntity<MyApiResponse<ProfileResponseDto>> getProfile(@AuthenticationPrincipal UserDetails currentUserDetails){
        ProfileResponseDto profile = profileService.getProfile((AppUser) currentUserDetails);
        return ResponseEntity.ok(MyApiResponse.success(profile, "Profile get successfully"));
    }

    @GetMapping("/posts")
    public ResponseEntity<MyApiResponse<?>> getMyPosts(
            @AuthenticationPrincipal UserDetails currentUserDetails,
            @ModelAttribute @Valid GetRecentPostsRequestDto requestDto
    ){
        Set<PostResponseDto> posts = profileService.getMyPosts((AppUser) currentUserDetails, requestDto);
        return ResponseEntity.ok(MyApiResponse.success(posts, "Posts get successfully"));
    }

}
