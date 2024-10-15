package com.example.server.service;

import com.example.server.model.Post;
import com.example.server.dto.response.CommentsResponseDto;
import com.example.server.dto.response.PostResponceDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
public interface PostService {
    Post savePost(HttpServletRequest request,
                  MultipartFile[] images,
                  MultipartFile video,
                  MultipartFile file,
                  String text
    );

    @Transactional
    ResponseEntity<Object> likePost(HttpServletRequest request, Long postId, byte like_type);

    //Like UserLikedPost(Long userId, Post saved_post);

    //PostLike ifUserLikedPost(Long userId, Post saved_post);

   // Like getUserLikeOnPost(Long userId, Long postId);

    //PostLike ifILikedThisPost(HttpServletRequest req, Long postId);

  //  List<PostResponceDto> getAllPosts();

    List<PostResponceDto> getAllPosts(HttpServletRequest req);

    PostResponceDto getPostDetails(Long postId);

    List<CommentsResponseDto> getAllCommentsOnPost(Long post_id);

    Post deletePost(HttpServletRequest servletRequest, Long post_id);

    Post updatePost(HttpServletRequest servletRequest, Long post_id, String text);
}
