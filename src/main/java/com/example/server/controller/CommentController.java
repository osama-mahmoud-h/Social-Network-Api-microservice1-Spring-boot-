package com.example.server.controller;

import com.example.server.dto.response.ResponseHandler;
import com.example.server.service.CommentService;
import com.example.server.dto.request.CommentRequestDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/comment")
public class CommentController {
    private final CommentService commentService;

    @PostMapping("write")
    public ResponseEntity<?> writeComment(HttpServletRequest request,
                                          @RequestBody CommentRequestDto commentDto
    ) {
        return commentService.writeComment(request,commentDto);
    }

    @PostMapping("like")
    public ResponseEntity<?> likeComment(HttpServletRequest request,
                                         Long commentId,
                                         byte like_type
    ){
        return ResponseHandler.generateResponse("comment like/unliked successfully",
                HttpStatus.OK,
                null
                ///commentService.likeComment(request,commentId,like_type)
        );
    }

    @DeleteMapping ("/delete/{comment_id}")
    public ResponseEntity<?> deleteComment(HttpServletRequest servletRequest,
                                           @PathVariable("comment_id") Long commentId
    ){
        return ResponseHandler.generateResponse("comment deleted successfully",
                HttpStatus.OK,
                commentService.deleteComment(servletRequest,commentId)
        );
    }

    @PutMapping("/update/{comment_id}")
    public ResponseEntity<?> updateComment(HttpServletRequest servletRequest,
                                           @PathVariable("comment_id") Long commentId,
                                           @RequestParam String text
    ){
        return ResponseHandler.generateResponse("comment deleted successfully",
                        HttpStatus.OK,
                        commentService.updateComment(servletRequest,commentId,text)
        );
    }

}
