package com.app.server.service;

import com.app.server.dto.request.comment.AddNewCommentRequestDto;
import com.app.server.dto.request.comment.GetAllCommentRepliesRequestDto;
import com.app.server.dto.request.comment.GetAllCommentsRequestDto;
import com.app.server.dto.request.comment.UpdateCommentRequestDto;
import com.app.server.dto.response.comment.CommentResponseDto;
import com.app.server.model.AppUser;

import java.util.Set;


public interface CommentService {
   // Object writeComment(AppUser currentUser, AddNewCommentRequestDto commentDto);

    boolean addNewComment(AppUser currentUser, AddNewCommentRequestDto commentDto);

    boolean deleteComment(AppUser currentUser, Long commentId);


    boolean updateComment(AppUser appUser, UpdateCommentRequestDto requestDto);

    Set<CommentResponseDto> getCommentsOnPost(AppUser appUser, GetAllCommentsRequestDto getAllCommentsRequestDto);

    boolean replayOnComment(AppUser appUser, AddNewCommentRequestDto addNewCommentRequestDto, Long commentId);

    Set<CommentResponseDto> getCommentReplies(AppUser appUser, GetAllCommentRepliesRequestDto getAllCommentsRequestDto);
}
