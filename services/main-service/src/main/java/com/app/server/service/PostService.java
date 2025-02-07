package com.app.server.service;

import com.app.server.dto.request.post.CreatePostRequestDto;
import com.app.server.dto.request.post.GetRecentPostsRequestDto;
import com.app.server.dto.request.post.UpdatePostRequestDto;
import com.app.server.model.AppUser;
import com.app.server.model.Post;
import com.app.server.dto.response.PostResponseDto;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public interface PostService {
    Post savePost(AppUser currentUser, CreatePostRequestDto createPostRequestDto);

    PostResponseDto getPostDetails(AppUser user, Long postId);

    boolean deletePost(AppUser user, Long postId);

    boolean updatePost(AppUser appUser, UpdatePostRequestDto requestDto);

    Set<PostResponseDto> getRecentPosts(AppUser currentUserDetails, GetRecentPostsRequestDto req);
}
