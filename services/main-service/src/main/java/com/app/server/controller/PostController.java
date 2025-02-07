package com.app.server.controller;

import com.app.server.dto.request.post.CreatePostRequestDto;
import com.app.server.dto.request.post.GetRecentPostsRequestDto;
import com.app.server.dto.request.post.UpdatePostRequestDto;
import com.app.server.dto.response.MyApiResponse;
import com.app.server.dto.response.PostResponseDto;
import com.app.server.model.AppUser;
import com.app.server.model.Post;
import com.app.server.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Set;


@RestController
@RequiredArgsConstructor

@RequestMapping("/api/v1/post")
public class PostController {
    private final PostService postService;

    @PostMapping(value = "/create",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MyApiResponse<Boolean>> savePost(@AuthenticationPrincipal UserDetails currentUser,
                                                          @Valid @ModelAttribute CreatePostRequestDto createPostRequestDto

    ) {
        Post savedPost = postService.savePost((AppUser)currentUser, createPostRequestDto);
        return ResponseEntity.ok(MyApiResponse.success( savedPost!=null, "Post created successfully"));
    }

    @GetMapping("/recent")
    public ResponseEntity<MyApiResponse<Set<PostResponseDto>>> allPosts(
            @AuthenticationPrincipal UserDetails currentUserDetails,
            @Valid @ModelAttribute GetRecentPostsRequestDto req
    ){
        Set<PostResponseDto> posts = postService.getRecentPosts((AppUser)currentUserDetails,req);
        return ResponseEntity.ok(MyApiResponse.success(posts,"all posts get successfully"));
    }


    @DeleteMapping("/delete/{postId}")
    public ResponseEntity<MyApiResponse<Boolean>> deletePost(
            @AuthenticationPrincipal UserDetails currentUserDetails,
            @PathVariable("postId") Long postId
    ){
        return ResponseEntity.ok(
                MyApiResponse.success(postService.deletePost((AppUser)currentUserDetails,postId),
                "post deleted successfully")
        );
    }

    @PutMapping("/update")
    public ResponseEntity<MyApiResponse<Boolean>> updatePost(
            @AuthenticationPrincipal UserDetails currentUserDetails,
            @Valid @RequestBody UpdatePostRequestDto requestDto
    ){
       return ResponseEntity.ok(
               MyApiResponse.success(postService.updatePost((AppUser)currentUserDetails,requestDto),
               "post updated successfully")
       );
    }

    @GetMapping("/get-details/{postId}")
    public ResponseEntity<MyApiResponse<PostResponseDto>> getPostDetails(
            @AuthenticationPrincipal UserDetails currentUserDetails,
            @PathVariable("postId") Long postId
    ){
        PostResponseDto postResponseDto = postService.getPostDetails((AppUser)currentUserDetails,postId);
       return ResponseEntity.ok(MyApiResponse.success(postResponseDto ,"post details get successfully"));
    }

}
