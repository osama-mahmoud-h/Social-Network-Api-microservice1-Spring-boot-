package com.example.server.service;

import com.example.server.dto.request.comment.AddNewCommentRequestDto;
import com.example.server.dto.request.comment.GetAllCommentsRequestDto;
import com.example.server.dto.request.comment.UpdateCommentRequestDto;
import com.example.server.dto.response.comment.CommentResponseDto;
import com.example.server.model.AppUser;
import com.example.server.model.Comment;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Set;


public interface CommentService {
   // Object writeComment(AppUser currentUser, AddNewCommentRequestDto commentDto);

    boolean addNewComment(AppUser currentUser, AddNewCommentRequestDto commentDto);

    boolean deleteComment(AppUser currentUser, Long commentId);


    boolean updateComment(AppUser appUser, UpdateCommentRequestDto requestDto);

    Set<CommentResponseDto> getCommentsOnPost(AppUser appUser, GetAllCommentsRequestDto getAllCommentsRequestDto);

}
