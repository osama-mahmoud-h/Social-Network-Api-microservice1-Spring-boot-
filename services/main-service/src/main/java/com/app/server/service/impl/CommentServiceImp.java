package com.app.server.service.impl;

import com.app.server.dto.request.comment.AddNewCommentRequestDto;
import com.app.server.dto.request.comment.GetAllCommentRepliesRequestDto;
import com.app.server.dto.request.comment.GetAllCommentsRequestDto;
import com.app.server.dto.request.comment.UpdateCommentRequestDto;
import com.app.server.dto.response.comment.CommentResponseDto;
import com.app.server.enums.CommentActionType;
import com.app.server.event.domain.CommentDomainEvent;
import com.app.server.exception.CustomRuntimeException;
import com.app.server.mapper.CommentMapper;
import com.app.server.model.Comment;
import com.app.server.model.UserProfile;
import com.app.server.service.CommentService;
import com.app.server.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImp implements CommentService {
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public boolean addNewComment(UserProfile currentUser, AddNewCommentRequestDto commentDto) {
        Comment newComment = commentMapper.mapAddNewCommentRequestDtoToComment(currentUser, commentDto);
        commentRepository.save(newComment);
        this.sendNewCommentNotification(newComment);
        return true;
    }

    @Override
    public boolean deleteComment(UserProfile currentUser, Long commentId) {
        Comment comment = this.getCommentById(commentId);
        int rowsAffected = commentRepository.deleteByIdAndAuthorId(currentUser.getUserId(), commentId);
        if(rowsAffected == 0){
            throw new CustomRuntimeException("Comment not found", HttpStatus.NOT_FOUND);
        }
        this.sendDeleteCommentNotification(comment);
        return true;
    }

    @Override
    public boolean updateComment(UserProfile userProfile, UpdateCommentRequestDto requestDto){
        Optional<Comment> comment = commentRepository.findByIdAndAuthorId(userProfile.getUserId(), requestDto.getCommentId());
        if(comment.isEmpty()){
            throw new CustomRuntimeException("Comment not found", HttpStatus.NOT_FOUND);
        }
        Comment mappedComment = commentMapper.mapUpdateCommentRequestDtoToComment(comment.get(), requestDto);
        commentRepository.save(mappedComment);

        this.sendUpdateCommentNotification(mappedComment);
        return true;
    }

    @Override
    public Set<CommentResponseDto> getCommentsOnPost(UserProfile userProfile, GetAllCommentsRequestDto requestDto){
        Pageable pageable = Pageable.ofSize(requestDto.getSize()).withPage(requestDto.getPage());
        return commentRepository.findCommentByPostId(requestDto.getPostId(), pageable)
                .stream()
                .map(commentMapper::mapCommentToCommentResponseDto)
                .collect(Collectors.toSet());
    }

    @Override
    public boolean replayOnComment(UserProfile userProfile, AddNewCommentRequestDto addNewCommentRequestDto, Long commentId) {
        Comment parentComment = this.getCommentById(commentId);
        if(parentComment.getParentComment() != null){
            throw new CustomRuntimeException("Cannot replay on a replay", HttpStatus.BAD_REQUEST);
        }

        Comment replyToComment = commentMapper.mapAddNewCommentRequestDtoToComment(userProfile, addNewCommentRequestDto);
        replyToComment.setParentComment(parentComment);
        commentRepository.save(replyToComment);

        this.sendReplyCommentNotification(replyToComment);
        return true;
    }

    @Override
    public Set<CommentResponseDto> getCommentReplies(UserProfile userProfile, GetAllCommentRepliesRequestDto getAllCommentsRequestDto) {
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

    /**
     * Publishes domain event for comment actions
     * Event will be asynchronously converted to Kafka message by DomainEventPublisher
     */
    private void publishCommentEvent(Comment comment, CommentActionType actionType){
        log.debug("Publishing comment domain event: action={}, commentId={}", actionType, comment.getCommentId());

        CommentDomainEvent event = new CommentDomainEvent(
            comment.getAuthor().getUserId(),
            actionType,
            comment
        );

        eventPublisher.publishEvent(event);
    }

    private void sendNewCommentNotification(Comment comment){
        this.publishCommentEvent(comment, CommentActionType.CREATE);
    }

    private void sendUpdateCommentNotification(Comment comment){
        this.publishCommentEvent(comment, CommentActionType.UPDATE);
    }

    private void sendDeleteCommentNotification(Comment comment){
        this.publishCommentEvent(comment, CommentActionType.DELETE);
    }

    private void sendReplyCommentNotification(Comment comment){
        this.publishCommentEvent(comment, CommentActionType.REPLY);
    }

}
