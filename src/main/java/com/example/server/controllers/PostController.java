package com.example.server.controllers;

import com.example.server.models.Post;
import com.example.server.payload.request.PostRequestDto;
import com.example.server.payload.response.ResponseHandler;
import com.example.server.services.PostService;
import com.example.server.services.impl.KafkaServiceImp;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor

@RequestMapping("/api/post")
public class PostController {
    private final PostService postService;
    private final KafkaServiceImp kafkaServiceImp;
    @PostMapping("/upload")
    public ResponseEntity<?> savePost(HttpServletRequest request,
                                           @RequestParam(value = "images",required = false) MultipartFile[] images,
                                           @RequestParam(value = "video",required = false)MultipartFile video,
                                           @RequestParam(value = "file",required = false)MultipartFile file,
                                           @RequestParam(value = "text",required = false)String text

    ) {
       // System.out.println("================================================================: "+images);
        Post post = postService.savePost(request,images, video,file, text);

        return ResponseHandler.generateResponse("post upload successfully",
                HttpStatus.CREATED,
                post
        );
    }

    @PostMapping("/like/{post_id}/{like_type}")
    public ResponseEntity<Object> likePost(HttpServletRequest request,
                                           @PathVariable("post_id") Long postId,
                                           @PathVariable("like_type") byte likeType
    ){

        return postService.likePost(request,postId,likeType);
    }

    @GetMapping("/all")
    public ResponseEntity<?> allPosts(HttpServletRequest req){
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
                postService.ifILikedThisPost(req,post_id));
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
