package com.example.server.services;

import com.example.server.models.Comment;
import com.example.server.models.CommentLike;
import com.example.server.payload.request.CommentRequestDto;
import com.example.server.payload.response.CommentsResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface CommentService {
    ResponseEntity<?> writeComment(HttpServletRequest request, CommentRequestDto commentDto);

    Comment deleteComment(HttpServletRequest servletRequest, Long commentId);

    Comment updateComment(HttpServletRequest servletRequest, Long commentId,String text);

    @Transactional
    CommentLike likeComment(HttpServletRequest request, Long commentId, byte like_type);

    List<CommentsResponseDto> getAllCommentsOnPost(HttpServletRequest req, Long post_id);
}
