package com.example.server.controllers;

import com.example.server.Exceptions.CustomErrorException;
import com.example.server.models.Comment;
import com.example.server.models.CommentLike;
import com.example.server.models.Post;
import com.example.server.models.PostLike;
import com.example.server.payload.response.CommentsResponseDto;
import com.example.server.payload.response.ResponseHandler;
import com.example.server.services.CommentService;
import com.example.server.payload.request.CommentRequestDto;
import com.example.server.services.CommentsReplayService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

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

    @PostMapping("/like/{comment_id}/{like_type}")
    public ResponseEntity<?> likeComment(HttpServletRequest request,
                                         @PathVariable("comment_id") Long commentId,
                                         @PathVariable("like_type") byte likeType
    ){
        return ResponseHandler.generateResponse("comment like / unlike",
                HttpStatus.OK,
                commentService.likeComment(request,commentId,likeType)
        );
    }

    @GetMapping("/all/{post_id}")
    public ResponseEntity<?> allCommentsOnPost(HttpServletRequest req,
                                               @PathVariable("post_id") Long post_id){
        System.out.println("post " + post_id);
        return ResponseHandler.generateResponse("all comments get successfully",
                HttpStatus.OK,
                commentService.getAllCommentsOnPost(req,post_id));
    }

}
/**
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 * */