package com.app.server.service.impl;

import com.app.server.dto.notification.post.PostEventDto;
import com.app.server.dto.request.post.CreatePostRequestDto;
import com.app.server.dto.request.post.GetRecentPostsRequestDto;
import com.app.server.dto.request.post.UpdatePostRequestDto;
import com.app.server.enums.KafkaTopics;
import com.app.server.enums.PostActionType;
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
import com.app.server.service.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
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
    private final NotificationService notificationService;
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
        Optional<Object> fetchRow = this.postRepository.findPostDetailsById(userId, postId);
        if(fetchRow.isEmpty()){
            throw new CustomRuntimeException("Post not found",HttpStatus.NOT_FOUND);
        }
         log.info("fetchRow {}", fetchRow.get());

         return postMapper.mapDbRowToPostResponseDto((Object[]) fetchRow.get());
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

        int rowAffected = this.postRepository.updatePostById(userId, requestDto.getPostId(), requestDto.getContent());
        if(rowAffected == 0){
            throw new CustomRuntimeException("Post not found",HttpStatus.NOT_FOUND);
        }
        this.sendPostUpdateNotification(post);
        return true;
    }

    @Override
    public Set<PostResponseDto> getRecentPosts(Long user, GetRecentPostsRequestDto req) {
        Pageable pageable = Pageable.ofSize(req.getSize()).withPage(req.getPage());

       return postRepository.findRecentPosts(user, pageable).stream()
                .map(postMapper::mapDbRowToPostResponseDto)
                .collect(Collectors.toSet());
    }

    private Set<File> uploadFiles(MultipartFile[] multipartFiles){
        if(multipartFiles == null || multipartFiles.length == 0){
            return Collections.emptySet();
        }
        return Arrays.stream(multipartFiles).map(this.fileMapper::mapMultiPartFileToFileSchema)
                .collect(Collectors.toSet());
    }

    private void sendPostNotification(PostActionType actionType, Post post) {
        PostEventDto postEventDto = PostEventDto.builder()
                .actionType(actionType)
                .post(post)
                .postId(post.getPostId())
                .build();

        System.out.println("Sending post notification: createdAt: " + post.getCreatedAt() + ", updatedAt: " + post.getUpdatedAt());

        this.notificationService.sendNotification(postEventDto, KafkaTopics.POST_EVENTS);
    }

    private void sendNewPostNotification(Post post) {
        sendPostNotification(PostActionType.CREATE, post);
    }

    private void sendPostDeleteNotification(Post post) {
        sendPostNotification(PostActionType.DELETE, post);
    }

    private void sendPostUpdateNotification(Post post) {
        sendPostNotification(PostActionType.UPDATE, post);
    }


}
