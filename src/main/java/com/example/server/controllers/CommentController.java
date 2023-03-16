package com.example.server.controllers;

import com.example.server.payload.response.ResponseHandler;
import com.example.server.services.CommentService;
import com.example.server.payload.request.CommentRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

    @PostMapping("like")
    public ResponseEntity<?> likeComment(HttpServletRequest request,
                                         Long commentId,
                                         byte like_type
    ){
        return ResponseHandler.generateResponse("comment like/unliked successfully",
                HttpStatus.OK,
                commentService.likeComment(request,commentId,like_type)
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
