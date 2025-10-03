package com.app.server.controller;

import com.app.server.dto.request.post.CreatePostRequestDto;
import com.app.server.dto.request.post.GetRecentPostsRequestDto;
import com.app.server.dto.request.post.UpdatePostRequestDto;
import com.app.server.dto.response.MyApiResponse;
import com.app.server.dto.response.PostResponseDto;
import com.app.server.model.AppUser;
import com.app.server.model.Post;
import com.app.server.service.PostService;
import com.app.shared.security.utils.SecurityUtils;
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
public class PostController {
    private final PostService postService;

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<MyApiResponse<Boolean>> savePost(@Valid @ModelAttribute CreatePostRequestDto createPostRequestDto
    ) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        Post savedPost = postService.savePost(currentUserId, createPostRequestDto);
        return ResponseEntity.ok(MyApiResponse.success( savedPost!=null, "Post created successfully"));
    }

    @GetMapping("/recent")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<MyApiResponse<Set<PostResponseDto>>> allPosts(
            @Valid @ModelAttribute GetRecentPostsRequestDto req
    ){
        Long currentUserId = SecurityUtils.getCurrentUserId();
        Set<PostResponseDto> posts = postService.getRecentPosts(currentUserId, req);
        return ResponseEntity.ok(MyApiResponse.success(posts,"all posts get successfully"));
    }


    @DeleteMapping("/delete/{postId}")
    @PreAuthorize("hasRole('ADMIN') or @postService.isPostOwner(#postId, authentication.details.userId)")
    public ResponseEntity<MyApiResponse<Boolean>> deletePost(
            @PathVariable("postId") Long postId
    ){
        Long currentUserId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(
                MyApiResponse.success(postService.deletePost(currentUserId, postId),
                "post deleted successfully")
        );
    }

    @PutMapping("/update")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<MyApiResponse<Boolean>> updatePost(
            @Valid @RequestBody UpdatePostRequestDto requestDto
    ){
        Long currentUserId = SecurityUtils.getCurrentUserId();
       return ResponseEntity.ok(
               MyApiResponse.success(postService.updatePost(currentUserId, requestDto),
               "post updated successfully")
       );
    }

    @GetMapping("/get-details/{postId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<MyApiResponse<PostResponseDto>> getPostDetails(
            @PathVariable("postId") Long postId
    ){
        Long currentUserId = SecurityUtils.getCurrentUserId();
        PostResponseDto postResponseDto = postService.getPostDetails(currentUserId, postId);
       return ResponseEntity.ok(MyApiResponse.success(postResponseDto ,"post details get successfully"));
    }

}
