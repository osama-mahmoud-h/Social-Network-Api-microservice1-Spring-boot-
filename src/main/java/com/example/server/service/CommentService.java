package com.example.server.service;

import com.example.server.model.Comment;
import com.example.server.dto.request.CommentRequestDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;


public interface CommentService {
    ResponseEntity<?> writeComment(HttpServletRequest request, CommentRequestDto commentDto);

    Comment deleteComment(HttpServletRequest servletRequest, Long commentId);

    Comment updateComment(HttpServletRequest servletRequest, Long commentId,String text);

    //CommentLike likeComment(HttpServletRequest request, Long commentId, byte like_type);
}
