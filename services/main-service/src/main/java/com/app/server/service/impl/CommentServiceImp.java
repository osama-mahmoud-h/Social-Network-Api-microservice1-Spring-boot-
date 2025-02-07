package com.app.server.service.impl;

import com.app.server.dto.request.comment.AddNewCommentRequestDto;
import com.app.server.dto.request.comment.GetAllCommentsRequestDto;
import com.app.server.dto.request.comment.UpdateCommentRequestDto;
import com.app.server.dto.response.comment.CommentResponseDto;
import com.app.server.exception.CustomRuntimeException;
import com.app.server.mapper.CommentMapper;
import com.app.server.model.*;
//import com.example.server.repository.CommentLikeRepository;
import com.app.server.service.CommentService;
import com.app.server.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentServiceImp implements CommentService {
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    @Override
    public boolean addNewComment(AppUser currentUser, AddNewCommentRequestDto commentDto) {
        Comment newComment = commentMapper.mapAddNewCommentRequestDtoToComment(currentUser, commentDto);
        commentRepository.save(newComment);
        return true;
    }

    @Override
    public boolean deleteComment(AppUser currentUser, Long commentId) {
        int rowsAffected = commentRepository.deleteByIdAndAuthorId(currentUser.getUserId(), commentId);
        if(rowsAffected == 0){
            throw new CustomRuntimeException("Comment not found", HttpStatus.NOT_FOUND);
        }
        return true;
    }

    @Override
    public boolean updateComment(AppUser appUser, UpdateCommentRequestDto requestDto){
        Optional<Comment> comment = commentRepository.findByIdAndAuthorId(appUser.getUserId(), requestDto.getCommentId());
        if(comment.isEmpty()){
            throw new CustomRuntimeException("Comment not found", HttpStatus.NOT_FOUND);
        }
        Comment mappedComment = commentMapper.mapUpdateCommentRequestDtoToComment(comment.get(), requestDto);
        commentRepository.save(mappedComment);
        return true;
    }

    @Override
    public Set<CommentResponseDto> getCommentsOnPost(AppUser appUser, GetAllCommentsRequestDto requestDto){
        Pageable pageable = Pageable.ofSize(requestDto.getSize()).withPage(requestDto.getPage());
        return commentRepository.findCommentByPostId(requestDto.getPostId(), pageable)
                .stream()
                .map(commentMapper::mapCommentToCommentResponseDto)
                .collect(Collectors.toSet());
    }

}
