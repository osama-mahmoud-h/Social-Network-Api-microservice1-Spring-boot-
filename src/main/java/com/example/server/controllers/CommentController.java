package com.example.server.controllers;

import com.example.server.services.CommentService;
import com.example.server.payload.request.CommentRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comment")
public class CommentController {
    private final CommentService commentService;

    @PostMapping("write")
    public ResponseEntity<?> writeComment(HttpServletRequest request,
                                          @RequestBody CommentRequestDto commentDto
    ) {
        return commentService.writeComment(request,commentDto);
    }

}
