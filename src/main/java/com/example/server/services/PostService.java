package com.example.server.services;

import com.example.server.Exceptions.CustomErrorException;
import com.example.server.models.Comment;
import com.example.server.models.Like;
import com.example.server.models.Post;
import com.example.server.models.User;
import com.example.server.payload.response.CommentsResponseDto;
import com.example.server.payload.response.PostResponceDto;
import com.example.server.payload.response.ResponseHandler;
import com.example.server.payload.response.UserResponceDto;
import com.example.server.repository.LikeRepository;
import com.example.server.repository.PostRepository;
import com.example.server.repository.UserRepository;
import com.example.server.security.jwt.AuthenticatedUser;
import com.example.server.services.impl.KafkaServiceImp;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;
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
    ResponseEntity<Object> likePost(HttpServletRequest request, Long postId, byte like_type);

    Like UserLikedPost(Long userId, Post saved_post);

    Like getUserLikeOnPost(Long userId, Long postId);

    List<PostResponceDto> getAllPosts();

    List<CommentsResponseDto> getAllCommentsOnPost(Long post_id);

    Post deletePost(HttpServletRequest servletRequest, Long post_id);

    Post updatePost(HttpServletRequest servletRequest, Long post_id, String text);
}
