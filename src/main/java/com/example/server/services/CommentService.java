package com.example.server.services;

import com.example.server.payload.request.CommentRequestDto;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;

public interface CommentService {
    ResponseEntity<?> writeComment(HttpServletRequest request, CommentRequestDto commentDto);
}
