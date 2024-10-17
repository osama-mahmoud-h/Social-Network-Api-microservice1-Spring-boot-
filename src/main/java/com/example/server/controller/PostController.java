package com.example.server.controller;

import com.example.server.dto.request.post.CreatePostRequestDto;
import com.example.server.dto.request.post.GetRecentPostsRequestDto;
import com.example.server.dto.response.MyApiResponse;
import com.example.server.model.AppUser;
import com.example.server.model.Post;
import com.example.server.dto.response.ResponseHandler;
import com.example.server.service.PostService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequiredArgsConstructor

@RequestMapping("/api/v1/post")
public class PostController {
    private final PostService postService;

    @PostMapping(value = "/create",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MyApiResponse<Boolean>> savePost(@AuthenticationPrincipal UserDetails currentUser,
                                                          @Valid @ModelAttribute CreatePostRequestDto createPostRequestDto

    ) {
        return ResponseEntity.ok(MyApiResponse.success( postService.savePost((AppUser)currentUser, createPostRequestDto)!=null, "Post created successfully"));
    }

    @PostMapping("/like/{post_id}/{like_type}")
    public ResponseEntity<Object> likePost(HttpServletRequest request,
                                           @PathVariable("post_id") Long postId,
                                           @PathVariable("like_type") byte likeType
    ){

        return postService.likePost(request,postId,likeType);
    }

    @GetMapping("/all")
    public ResponseEntity<?> allPosts(@AuthenticationPrincipal UserDetails currentUser, @Valid @ModelAttribute GetRecentPostsRequestDto req){
        return ResponseHandler.generateResponse("all posts successfully",
                HttpStatus.OK,
                 postService.getAllPosts(req));
    }

    @GetMapping("/comments/all/{post_id}")
    public ResponseEntity<?> allCommentsOnPost(@PathVariable("post_id") Long post_id){
        return ResponseHandler.generateResponse("all comments get successfully",
                HttpStatus.OK,
                postService.getAllCommentsOnPost(post_id));
    }
    @GetMapping("/user-like/{post_id}")
    public ResponseEntity <?> ifILikedThisPost(HttpServletRequest req,
                                           @PathVariable("post_id") Long post_id
    ){
        return ResponseHandler.generateResponse("user like on post",
                HttpStatus.OK,
               false
               // postService.ifILikedThisPost(req,post_id)
                );
    }

    @DeleteMapping("/delete/{post_id}")
    public ResponseEntity<?> deletePost(HttpServletRequest servletRequest,
                                        @PathVariable("post_id") Long post_id
    ){
        return ResponseHandler.generateResponse("post delete successfully",
                HttpStatus.OK,
                postService.deletePost(servletRequest,post_id));
    }

    @PutMapping("/update/{post_id}")
    public ResponseEntity<?> updatePost(HttpServletRequest servletRequest,
                                        @PathVariable("post_id") Long post_id,
                                        @RequestParam String text
    ){
        return ResponseHandler.generateResponse("post updated successfully",
                HttpStatus.OK,
                postService.updatePost(servletRequest,post_id,text));
    }

    @GetMapping("/get-details/{post_id}")
    public ResponseEntity<?> getPostDetails(@PathVariable("post_id") Long postId){
        return ResponseHandler.generateResponse("post details get successfully",
                HttpStatus.OK,
                postService.getPostDetails(postId));
    }

}
