package com.app.server.service.impl;

import com.app.server.dto.request.comment.AddNewCommentRequestDto;
import com.app.server.dto.request.comment.GetAllCommentRepliesRequestDto;
import com.app.server.dto.request.comment.GetAllCommentsRequestDto;
import com.app.server.dto.request.comment.UpdateCommentRequestDto;
import com.app.server.dto.response.comment.CommentResponseDto;
import com.app.shared.events.type.CommentActionType;
import com.app.server.event.app.domain.CommentDomainEvent;
import com.app.server.exception.CustomRuntimeException;
import com.app.server.mapper.CommentMapper;
import com.app.server.model.Comment;
import com.app.server.model.Post;
import com.app.server.model.UserProfile;
import com.app.server.repository.PostRepository;
import com.app.server.service.CommentService;
import com.app.server.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImp implements CommentService {
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final PostRepository postRepository;
    private final ApplicationEventPublisher eventPublisher;

    // Each mutation is: load the aggregate → call the transition → save. Who may edit or delete a
    // comment, and what counts as valid content, live in Comment rather than in a query.

    @Override
    @Transactional
    public boolean addNewComment(UserProfile currentUser, AddNewCommentRequestDto commentDto) {
        Post post = postRepository.findById(commentDto.getPostId())
                .orElseThrow(() -> new CustomRuntimeException("Post not found", HttpStatus.NOT_FOUND));

        Comment newComment = Comment.writeOn(post, currentUser, commentDto.getContent());
        commentRepository.save(newComment);
        this.sendNewCommentNotification(newComment);
        return true;
    }

    @Override
    @Transactional
    public boolean deleteComment(UserProfile currentUser, Long commentId) {
        Comment comment = this.getCommentById(commentId);

        comment.deleteBy(currentUser.getUserId());
        commentRepository.delete(comment);

        this.sendDeleteCommentNotification(comment);
        return true;
    }

    @Override
    @Transactional
    public boolean updateComment(UserProfile userProfile, UpdateCommentRequestDto requestDto){
        Comment comment = this.getCommentById(requestDto.getCommentId());

        comment.edit(userProfile.getUserId(), requestDto.getContent());
        commentRepository.save(comment);

        this.sendUpdateCommentNotification(comment);
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
    @Transactional
    public boolean replayOnComment(UserProfile userProfile, AddNewCommentRequestDto addNewCommentRequestDto, Long commentId) {
        Comment parentComment = this.getCommentById(commentId);

        Comment reply = Comment.replyTo(parentComment, userProfile, addNewCommentRequestDto.getContent());
        commentRepository.save(reply);

        this.sendReplyCommentNotification(reply);
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
