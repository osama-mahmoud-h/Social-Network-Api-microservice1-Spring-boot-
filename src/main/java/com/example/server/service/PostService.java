package com.example.server.service;

import com.example.server.dto.request.post.CreatePostRequestDto;
import com.example.server.dto.request.post.GetRecentPostsRequestDto;
import com.example.server.dto.request.post.UpdatePostRequestDto;
import com.example.server.model.AppUser;
import com.example.server.model.Post;
import com.example.server.dto.response.CommentsResponseDto;
import com.example.server.dto.response.PostResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public interface PostService {
    Post savePost(AppUser currentUser, CreatePostRequestDto createPostRequestDto);

    PostResponseDto getPostDetails(AppUser user, Long postId);

    boolean deletePost(AppUser user, Long postId);

    boolean updatePost(AppUser appUser, UpdatePostRequestDto requestDto);

    Set<PostResponseDto> getRecentPosts(AppUser currentUserDetails, GetRecentPostsRequestDto req);
}
