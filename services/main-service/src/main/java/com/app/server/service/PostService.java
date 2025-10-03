package com.app.server.service;

import com.app.server.dto.request.post.CreatePostRequestDto;
import com.app.server.dto.request.post.GetRecentPostsRequestDto;
import com.app.server.dto.request.post.UpdatePostRequestDto;
import com.app.server.model.Post;
import com.app.server.dto.response.PostResponseDto;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public interface PostService {
    Post savePost(Long currentUser, CreatePostRequestDto createPostRequestDto);

    PostResponseDto getPostDetails(Long user, Long postId);

    boolean deletePost(Long user, Long postId);

    boolean updatePost(Long appUser, UpdatePostRequestDto requestDto);

    Set<PostResponseDto> getRecentPosts(Long currentUserDetails, GetRecentPostsRequestDto req);
}
