package com.app.server.controller;

import com.app.server.dto.request.post.CreatePostRequestDto;
import com.app.server.dto.request.post.GetRecentPostsRequestDto;
import com.app.server.dto.request.post.UpdatePostRequestDto;
import com.app.server.dto.response.MyApiResponse;
import com.app.server.dto.response.PostResponseDto;
import com.app.server.model.Post;
import com.app.server.service.PostService;
import com.app.shared.security.utils.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Set;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/post")
@Tag(name = "Posts", description = "APIs for managing posts")
@SecurityRequirement(name = "jwtAuth")
public class PostController {
    private final PostService postService;

    @Operation(summary = "Create a new post", description = "Create a new post with text and optional images")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Post created successfully",
            content = @Content(schema = @Schema(implementation = MyApiResponse.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized",
            content = @Content(schema = @Schema(implementation = MyApiResponse.class)))
    })
    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<MyApiResponse<Boolean>> savePost(@Valid @ModelAttribute CreatePostRequestDto createPostRequestDto
    ) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        System.out.println("currentUserId = " + currentUserId);
        Post savedPost = postService.savePost(currentUserId, createPostRequestDto);
        return ResponseEntity.ok(MyApiResponse.success("Post created successfully", savedPost != null));
    }

    @Operation(summary = "Get recent posts", description = "Retrieve recent posts for the feed")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Posts retrieved successfully",
            content = @Content(schema = @Schema(implementation = MyApiResponse.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized",
            content = @Content(schema = @Schema(implementation = MyApiResponse.class)))
    })
    @GetMapping("/recent")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<MyApiResponse<Set<PostResponseDto>>> allPosts(
            @Valid @ModelAttribute GetRecentPostsRequestDto req
    ){
        Long currentUserId = SecurityUtils.getCurrentUserId();
        System.out.println("currentUserId = " + currentUserId);
        Set<PostResponseDto> posts = postService.getRecentPosts(currentUserId, req);
        return ResponseEntity.ok(MyApiResponse.success("All posts retrieved successfully", posts));
    }

    @Operation(summary = "Delete a post", description = "Delete a post by ID (owner or admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Post deleted successfully",
            content = @Content(schema = @Schema(implementation = MyApiResponse.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized",
            content = @Content(schema = @Schema(implementation = MyApiResponse.class))),
        @ApiResponse(responseCode = "403", description = "Forbidden - not post owner",
            content = @Content(schema = @Schema(implementation = MyApiResponse.class)))
    })
    @DeleteMapping("/delete/{postId}")
    @PreAuthorize("hasRole('ADMIN') or @postService.isPostOwner(#postId, authentication.details.userId)")
    public ResponseEntity<MyApiResponse<Boolean>> deletePost(
            @Parameter(description = "Post ID to delete") @PathVariable("postId") Long postId
    ){
        Long currentUserId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(
                MyApiResponse.success("Post deleted successfully",
                        postService.deletePost(currentUserId, postId))
        );
    }

    @Operation(summary = "Update a post", description = "Update post content")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Post updated successfully",
            content = @Content(schema = @Schema(implementation = MyApiResponse.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized",
            content = @Content(schema = @Schema(implementation = MyApiResponse.class)))
    })
    @PutMapping("/update")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<MyApiResponse<Boolean>> updatePost(
            @Valid @RequestBody UpdatePostRequestDto requestDto
    ){
        Long currentUserId = SecurityUtils.getCurrentUserId();
       return ResponseEntity.ok(
               MyApiResponse.success("Post updated successfully",
                       postService.updatePost(currentUserId, requestDto))
       );
    }

    @Operation(summary = "Get post details", description = "Retrieve detailed information about a specific post")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Post details retrieved successfully",
            content = @Content(schema = @Schema(implementation = MyApiResponse.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized",
            content = @Content(schema = @Schema(implementation = MyApiResponse.class))),
        @ApiResponse(responseCode = "404", description = "Post not found",
            content = @Content(schema = @Schema(implementation = MyApiResponse.class)))
    })
    @GetMapping("/get-details/{postId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<MyApiResponse<PostResponseDto>> getPostDetails(
            @Parameter(description = "Post ID to retrieve") @PathVariable("postId") Long postId
    ){
        Long currentUserId = SecurityUtils.getCurrentUserId();
        PostResponseDto postResponseDto = postService.getPostDetails(currentUserId, postId);
       return ResponseEntity.ok(MyApiResponse.success("Post details retrieved successfully", postResponseDto));
    }

}