package com.example.server.services;

import com.example.server.models.PostLike;
import com.example.server.models.Post;
import com.example.server.payload.response.CommentsResponseDto;
import com.example.server.payload.response.PostResponceDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
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
    String likePost(HttpServletRequest request, Long postId, byte like_type);

    //Like UserLikedPost(Long userId, Post saved_post);

    PostLike ifUserLikedPost(Long userId, Post saved_post);

   // Like getUserLikeOnPost(Long userId, Long postId);

    PostLike ifILikedThisPost(HttpServletRequest req, Long postId);

  //  List<PostResponceDto> getAllPosts();

    List<PostResponceDto> getAllPosts(HttpServletRequest req);

    PostResponceDto getPostDetails(Long postId);

    List<CommentsResponseDto> getAllCommentsOnPost(Long post_id);

    Post deletePost(HttpServletRequest servletRequest, Long post_id);

    Post updatePost(HttpServletRequest servletRequest, Long post_id, String text);
}
