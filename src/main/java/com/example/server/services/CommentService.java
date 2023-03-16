package com.example.server.services;

import com.example.server.models.Comment;
import com.example.server.models.CommentLike;
import com.example.server.payload.request.CommentRequestDto;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;

public interface CommentService {
    ResponseEntity<?> writeComment(HttpServletRequest request, CommentRequestDto commentDto);

    Comment deleteComment(HttpServletRequest servletRequest, Long commentId);

    Comment updateComment(HttpServletRequest servletRequest, Long commentId,String text);

    CommentLike likeComment(HttpServletRequest request, Long commentId, byte like_type);
}
