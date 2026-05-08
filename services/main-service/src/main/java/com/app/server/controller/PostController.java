package com.app.server.controller;

import com.app.server.controller.swagger.IPostApi;
import com.app.server.dto.request.post.CreatePostRequestDto;
import com.app.server.dto.request.post.GetRecentPostsRequestDto;
import com.app.server.dto.request.post.UpdatePostRequestDto;
import com.app.server.dto.response.PostResponseDto;
import com.app.server.model.Post;
import com.app.server.service.PostService;
import com.app.shared.security.dto.MyApiResponse;
import com.app.shared.security.utils.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/post")
public class PostController implements IPostApi {
    private final PostService postService;

    @Override
    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<MyApiResponse<Boolean>> savePost(@Valid @ModelAttribute CreatePostRequestDto createPostRequestDto
    ) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        log.info("currentUserId = {}" , currentUserId);
        Post savedPost = postService.savePost(currentUserId, createPostRequestDto);
        return ResponseEntity.ok(MyApiResponse.success("Post created successfully", savedPost != null));
    }

    @Override
    @GetMapping("/recent")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<MyApiResponse<List<PostResponseDto>>> allPosts(
            @Valid @ModelAttribute GetRecentPostsRequestDto req
    ){
        Long currentUserId = SecurityUtils.getCurrentUserId();
        log.debug("currentUserId = {}" , currentUserId);
        List<PostResponseDto> posts = postService.getRecentPosts(currentUserId, req);
        return ResponseEntity.ok(MyApiResponse.success("All posts retrieved successfully", posts));
    }

    @Override
    @DeleteMapping("/delete/{postId}")
    @PreAuthorize("hasRole('ADMIN') or @postService.isPostOwner(#postId, authentication.details.userId)")
    public ResponseEntity<MyApiResponse<Boolean>> deletePost(
            @PathVariable("postId") Long postId
    ){
        Long currentUserId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(
                MyApiResponse.success("Post deleted successfully",
                        postService.deletePost(currentUserId, postId))
        );
    }

    @Override
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

    @Override
    @GetMapping("/get-details/{postId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<MyApiResponse<PostResponseDto>> getPostDetails(
            @PathVariable("postId") Long postId
    ){
        Long currentUserId = SecurityUtils.getCurrentUserId();
        PostResponseDto postResponseDto = postService.getPostDetails(currentUserId, postId);
       return ResponseEntity.ok(MyApiResponse.success("Post details retrieved successfully", postResponseDto));
    }

}