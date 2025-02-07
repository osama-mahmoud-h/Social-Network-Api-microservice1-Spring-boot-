package com.app.server.service.impl;

import com.app.server.dto.request.post.CreatePostRequestDto;
import com.app.server.dto.request.post.GetRecentPostsRequestDto;
import com.app.server.dto.request.post.UpdatePostRequestDto;
import com.app.server.exception.CustomRuntimeException;
import com.app.server.mapper.FileMapper;
import com.app.server.mapper.PostMapper;
import com.app.server.model.AppUser;
import com.app.server.model.File;
import com.app.server.model.Post;
import com.app.server.dto.response.PostResponseDto;
//import com.example.server.repository.PostLikeRepository;
import com.app.server.repository.FileRepository;
import com.app.server.repository.PostRepository;
import com.app.server.service.PostService;
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


    @Override
    @Transactional
    public Post savePost(AppUser currentUser, CreatePostRequestDto createPostRequestDto){
        Post newPost = this.postMapper.mapCreatePostRequestDtoToPost(createPostRequestDto);
        Set<File> uploadedFiles = this.uploadFiles(createPostRequestDto.getFiles());

        List<File> savedFiles = this.fileRepository.saveAll(uploadedFiles);

        newPost.setFiles(new HashSet<>(savedFiles));
        newPost.setAuthor(currentUser);

        return this.postRepository.save(newPost);
    }


     @Override
     public PostResponseDto getPostDetails(AppUser user, Long postId){
        Optional<Object> fetchRow = this.postRepository.findPostDetailsById(user.getUserId(), postId);
        if(fetchRow.isEmpty()){
            throw new CustomRuntimeException("Post not found",HttpStatus.NOT_FOUND);
        }
         log.info("fetchRow {}", fetchRow.get());

         return postMapper.mapDbRowToPostResponseDto((Object[]) fetchRow.get());
    }


    @Override
    public boolean deletePost(AppUser user, Long postId){
        int rowAffected = this.postRepository.deletePostById(user.getUserId(), postId);
        if(rowAffected == 0){
            throw new CustomRuntimeException("Post not found",HttpStatus.NOT_FOUND);
        }
        return true;
    }


    @Override
    public boolean updatePost(AppUser appUser, UpdatePostRequestDto requestDto) {
        int rowAffected = this.postRepository.updatePostById(appUser.getUserId(), requestDto.getPostId(), requestDto.getContent());
        if(rowAffected == 0){
            throw new CustomRuntimeException("Post not found",HttpStatus.NOT_FOUND);
        }
        return true;
    }

    @Override
    public Set<PostResponseDto> getRecentPosts(AppUser user, GetRecentPostsRequestDto req) {
        Pageable pageable = Pageable.ofSize(req.getSize()).withPage(req.getPage());

       return postRepository.findRecentPosts(user.getUserId(), pageable).stream()
                .map(postMapper::mapDbRowToPostResponseDto)
                .collect(Collectors.toSet());
    }

    private Set<File> uploadFiles(MultipartFile[] multipartFiles){
        return Arrays.stream(multipartFiles).map(this.fileMapper::mapMultiPartFileToFileSchema)
                .collect(Collectors.toSet());
    }
}
