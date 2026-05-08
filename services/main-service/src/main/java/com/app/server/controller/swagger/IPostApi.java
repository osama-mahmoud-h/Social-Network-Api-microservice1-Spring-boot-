package com.app.server.controller.swagger;

import com.app.server.dto.request.post.CreatePostRequestDto;
import com.app.server.dto.request.post.GetRecentPostsRequestDto;
import com.app.server.dto.request.post.UpdatePostRequestDto;
import com.app.server.dto.response.PostResponseDto;
import com.app.shared.security.dto.MyApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Tag(name = "Posts", description = "APIs for managing posts")
@SecurityRequirement(name = "jwtAuth")
public interface IPostApi {

    @Operation(summary = "Create a new post", description = "Create a new post with text and optional images")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Post created successfully",
                    content = @Content(schema = @Schema(implementation = MyApiResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = MyApiResponse.class)))
    })
    ResponseEntity<MyApiResponse<Boolean>> savePost(
            @Valid @ModelAttribute CreatePostRequestDto createPostRequestDto);

    @Operation(summary = "Get recent posts", description = "Retrieve recent posts for the feed")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Posts retrieved successfully",
                    content = @Content(schema = @Schema(implementation = MyApiResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = MyApiResponse.class)))
    })
    ResponseEntity<MyApiResponse<List<PostResponseDto>>> allPosts(
            @Valid @ModelAttribute GetRecentPostsRequestDto req);

    @Operation(summary = "Delete a post", description = "Delete a post by ID (owner or admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Post deleted successfully",
                    content = @Content(schema = @Schema(implementation = MyApiResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = MyApiResponse.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden - not post owner",
                    content = @Content(schema = @Schema(implementation = MyApiResponse.class)))
    })
    ResponseEntity<MyApiResponse<Boolean>> deletePost(
            @Parameter(description = "Post ID to delete") @PathVariable("postId") Long postId);

    @Operation(summary = "Update a post", description = "Update post content")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Post updated successfully",
                    content = @Content(schema = @Schema(implementation = MyApiResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = MyApiResponse.class)))
    })
    ResponseEntity<MyApiResponse<Boolean>> updatePost(
            @Valid @RequestBody UpdatePostRequestDto requestDto);

    @Operation(summary = "Get post details", description = "Retrieve detailed information about a specific post")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Post details retrieved successfully",
                    content = @Content(schema = @Schema(implementation = MyApiResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = MyApiResponse.class))),
            @ApiResponse(responseCode = "404", description = "Post not found",
                    content = @Content(schema = @Schema(implementation = MyApiResponse.class)))
    })
    ResponseEntity<MyApiResponse<PostResponseDto>> getPostDetails(
            @Parameter(description = "Post ID to retrieve") @PathVariable("postId") Long postId);
}