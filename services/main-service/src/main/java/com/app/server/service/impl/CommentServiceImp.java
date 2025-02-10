package com.app.server.service.impl;

import com.app.server.dto.notification.NotificationEvent;
import com.app.server.dto.request.comment.AddNewCommentRequestDto;
import com.app.server.dto.request.comment.GetAllCommentRepliesRequestDto;
import com.app.server.dto.request.comment.GetAllCommentsRequestDto;
import com.app.server.dto.request.comment.UpdateCommentRequestDto;
import com.app.server.dto.response.comment.CommentResponseDto;
import com.app.server.enums.NotificationType;
import com.app.server.exception.CustomRuntimeException;
import com.app.server.mapper.CommentMapper;
import com.app.server.model.*;
//import com.example.server.repository.CommentLikeRepository;
import com.app.server.service.CommentService;
import com.app.server.repository.CommentRepository;
import com.app.server.service.notification.NotificationService;
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
    private final NotificationService notificationService;

    @Override
    public boolean addNewComment(AppUser currentUser, AddNewCommentRequestDto commentDto) {
        Comment newComment = commentMapper.mapAddNewCommentRequestDtoToComment(currentUser, commentDto);
        commentRepository.save(newComment);
        this.sendNewCommentNotification(currentUser, newComment);
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

    @Override
    public boolean replayOnComment(AppUser appUser, AddNewCommentRequestDto addNewCommentRequestDto, Long commentId) {
        Comment parentComment = this.getCommentById(commentId);
        if(parentComment.getParentComment() != null){
            throw new CustomRuntimeException("Cannot replay on a replay", HttpStatus.BAD_REQUEST);
        }

        Comment replyToComment = commentMapper.mapAddNewCommentRequestDtoToComment(appUser, addNewCommentRequestDto);
        replyToComment.setParentComment(parentComment);
        commentRepository.save(replyToComment);

        this.sendReplyCommentNotification(appUser, replyToComment);

        return true;
    }

    @Override
    public Set<CommentResponseDto> getCommentReplies(AppUser appUser, GetAllCommentRepliesRequestDto getAllCommentsRequestDto) {
        Pageable pageable = Pageable.ofSize(getAllCommentsRequestDto.getSize()).withPage(getAllCommentsRequestDto.getPage());
        return commentRepository.findCommentByParentCommentId(getAllCommentsRequestDto.getCommentId(), pageable)
                .stream()
                .map(commentMapper::mapCommentToCommentResponseDto)
                .collect(Collectors.toSet());
    }

    private Comment getCommentById(Long commentId){
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomRuntimeException("Comment not found", HttpStatus.NOT_FOUND));
    }

    private void sendNewCommentNotification(AppUser currentUser, Comment comment){
        NotificationEvent notificationEvent = NotificationEvent.builder()
                .type(NotificationType.COMMENTED_YOUR_POST)
                .senderId(currentUser.getUserId())
                .receiverId(comment.getPost().getAuthor().getUserId())
                .message(currentUser.getUserId() + " commented on your post")
                .build();

        notificationService.sendNotification(notificationEvent);
    }

    private void sendReplyCommentNotification(AppUser currentUser, Comment comment){
        NotificationEvent notificationEvent = NotificationEvent.builder()
                .type(NotificationType.REPLIED_TO_YOUR_COMMENT)
                .senderId(currentUser.getUserId())
                .receiverId(comment.getParentComment().getAuthor().getUserId())
                .message(currentUser.getUserId() + " replied on your comment")
                .build();

        notificationService.sendNotification(notificationEvent);
    }

}
