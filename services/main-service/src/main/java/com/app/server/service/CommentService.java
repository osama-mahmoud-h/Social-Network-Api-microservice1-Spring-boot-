package com.app.server.service;

import com.app.server.dto.request.comment.AddNewCommentRequestDto;
import com.app.server.dto.request.comment.GetAllCommentRepliesRequestDto;
import com.app.server.dto.request.comment.GetAllCommentsRequestDto;
import com.app.server.dto.request.comment.UpdateCommentRequestDto;
import com.app.server.dto.response.comment.CommentResponseDto;
import com.app.server.model.UserProfile;

import java.util.Set;


public interface CommentService {
   // Object writeComment(UserProfile currentUser, AddNewCommentRequestDto commentDto);

    boolean addNewComment(UserProfile currentUser, AddNewCommentRequestDto commentDto);

    boolean deleteComment(UserProfile currentUser, Long commentId);


    boolean updateComment(UserProfile userProfile, UpdateCommentRequestDto requestDto);

    Set<CommentResponseDto> getCommentsOnPost(UserProfile userProfile, GetAllCommentsRequestDto getAllCommentsRequestDto);

    boolean replayOnComment(UserProfile userProfile, AddNewCommentRequestDto addNewCommentRequestDto, Long commentId);

    Set<CommentResponseDto> getCommentReplies(UserProfile userProfile, GetAllCommentRepliesRequestDto getAllCommentsRequestDto);
}
