package com.app.server.service.impl;

import com.app.server.dto.request.post.CreatePostRequestDto;
import com.app.server.dto.request.post.GetRecentPostsRequestDto;
import com.app.server.dto.request.post.UpdatePostRequestDto;
import com.app.server.enums.PostActionType;
import com.app.server.event.app.domain.PostDomainEvent;
import com.app.server.exception.CustomRuntimeException;
import com.app.server.mapper.FileMapper;
import com.app.server.mapper.PostMapper;
import com.app.server.model.File;
import com.app.server.model.Post;
import com.app.server.dto.response.PostResponseDto;
import com.app.server.model.UserProfile;
import com.app.server.repository.FileRepository;
import com.app.server.repository.PostRepository;
import com.app.server.repository.UserProfileRepository;
import com.app.server.service.PostService;
import com.app.server.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostServiceImp implements PostService {
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final FileMapper fileMapper;
    private final FileRepository fileRepository;
    private final ApplicationEventPublisher eventPublisher;  // Changed from NotificationService
    private final UserService userService;
    private final UserProfileRepository userProfileRepository;

    @Override
    @Transactional
    public Post savePost(Long currentUser, CreatePostRequestDto createPostRequestDto){
        Post newPost = this.postMapper.mapCreatePostRequestDtoToPost(createPostRequestDto);
        UserProfile author = userProfileRepository.getAppUsersByUserId(currentUser)
                .orElseThrow(() -> new CustomRuntimeException("User not found", HttpStatus.NOT_FOUND));

        Set<File> uploadedFiles = this.uploadFiles(createPostRequestDto.getFiles());

        List<File> savedFiles = this.fileRepository.saveAll(uploadedFiles);

        newPost.setFiles(new HashSet<>(savedFiles));
        newPost.setAuthor(author);

        this.postRepository.save(newPost);
        this.sendNewPostNotification(newPost);
        //TODO :return post response dto
        return null;
    }


     @Override
     public PostResponseDto getPostDetails(Long userId, Long postId){
        return this.postRepository.findPostDetailsById(userId, postId)
                .map(postMapper::mapProjectionToPostResponseDto)
                .orElseThrow(() -> new CustomRuntimeException("Post not found", HttpStatus.NOT_FOUND));
    }


    @Override
    public boolean deletePost(Long userId, Long postId){
        Post post = this.postRepository.findById(postId).
                orElseThrow(() -> new CustomRuntimeException("Post not found",HttpStatus.NOT_FOUND));
        int rowAffected = this.postRepository.deletePostById(userId, postId);
        if(rowAffected == 0){
            throw new CustomRuntimeException("Post not found",HttpStatus.NOT_FOUND);
        }

        this.sendPostDeleteNotification(post);

        return true;
    }


    @Override
    public boolean updatePost(Long userId, UpdatePostRequestDto requestDto) {
        Post post = this.postRepository.findById(requestDto.getPostId())
                .orElseThrow(() -> new CustomRuntimeException("Post not found", HttpStatus.NOT_FOUND));

        // Verify the user is the author
        if (!post.getAuthor().getUserId().equals(userId)) {
            throw new CustomRuntimeException("Unauthorized to update this post", HttpStatus.FORBIDDEN);
        }

        // Update content
        post.setContent(requestDto.getContent());

        // Update publicity if provided
        if (requestDto.getPublicity() != null) {
            post.setPublicity(requestDto.getPublicity());
        }

        this.postRepository.save(post);
        this.sendPostUpdateNotification(post);
        return true;
    }

    @Override
    public List<PostResponseDto> getRecentPosts(Long user, GetRecentPostsRequestDto req) {
        int limit = req.getSize();
        int offset = req.getPage() * req.getSize();

        return postRepository.findRecentPosts(user, limit, offset).stream()
                .map(postMapper::mapProjectionToPostResponseDto)
                .collect(Collectors.toList());
    }

    private Set<File> uploadFiles(MultipartFile[] multipartFiles){
        if(multipartFiles == null || multipartFiles.length == 0){
            return Collections.emptySet();
        }
        return Arrays.stream(multipartFiles).map(this.fileMapper::mapMultiPartFileToFileSchema)
                .collect(Collectors.toSet());
    }

    private void publishPostEvent(PostActionType actionType, Post post) {
        log.debug("Publishing post domain event: action={}, postId={}", actionType, post.getPostId());

        PostDomainEvent event = new PostDomainEvent(
            post.getAuthor().getUserId(),
            actionType,
            post
        );

        eventPublisher.publishEvent(event);
    }

    private void sendNewPostNotification(Post post) {
        publishPostEvent(PostActionType.CREATE, post);
    }

    private void sendPostDeleteNotification(Post post) {
        publishPostEvent(PostActionType.DELETE, post);
    }

    private void sendPostUpdateNotification(Post post) {
        publishPostEvent(PostActionType.UPDATE, post);
    }


}
