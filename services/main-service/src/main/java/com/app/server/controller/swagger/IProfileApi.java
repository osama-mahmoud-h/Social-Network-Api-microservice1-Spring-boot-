package com.app.server.controller.swagger;

import com.app.server.dto.request.post.GetRecentPostsRequestDto;
import com.app.server.dto.request.profile.UpdateProfileBioRequestDto;
import com.app.server.dto.response.profile.ProfileResponseDto;
import com.app.shared.security.dto.MyApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Profile", description = "APIs for managing user profiles")
@SecurityRequirement(name = "jwtAuth")
public interface IProfileApi {

    @Operation(summary = "Update profile bio", description = "Update user's profile biography")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bio updated successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    ResponseEntity<MyApiResponse<Boolean>> updateBio(
            @RequestBody UpdateProfileBioRequestDto bioRequestDto);

    @Operation(summary = "Update profile image", description = "Update user's profile picture")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Image updated successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    ResponseEntity<MyApiResponse<Boolean>> updateImage(
            @RequestParam("image") MultipartFile image);

    @Operation(summary = "Get profile", description = "Retrieve current user's profile information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    ResponseEntity<MyApiResponse<ProfileResponseDto>> getProfile();

    @Operation(summary = "Get user's posts", description = "Retrieve all posts created by the current user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Posts retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    ResponseEntity<MyApiResponse<?>> getMyPosts(
            @ModelAttribute @Valid GetRecentPostsRequestDto requestDto);
}