package com.example.server.controllers;


import com.example.server.payload.response.ResponseHandler;
import com.example.server.services.CommentsReplayService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comment/replay")
@CrossOrigin(origins = "*", maxAge = 3600)

public class CommentReplayController {
    private final CommentsReplayService commentsReplayService;

    @PostMapping("/write/{comment_id}")
    public ResponseEntity<?> replayComment(HttpServletRequest servletRequest,
                                           @PathVariable("comment_id") Long commentId ,
                                           String text
    ){
        return ResponseHandler.generateResponse("replay done",
                HttpStatus.OK,
                commentsReplayService.replayComment(servletRequest,commentId,text)
        );
    }

    @PutMapping("/update/{comment_replay_id}")
    public ResponseEntity<?> updateReplayOnComment(HttpServletRequest servletRequest,
                                           @PathVariable("comment_replay_id") Long commentReplayId ,
                                           String text
    ){
        return ResponseHandler.generateResponse("replay updated successfully",
                HttpStatus.OK,
                commentsReplayService.updateReplayOnComment(servletRequest,commentReplayId,text)
        );
    }

    @DeleteMapping("/{comment_replay_id}")
    public ResponseEntity<?> deleteReplayOnComment(HttpServletRequest servletRequest,
                                          @PathVariable("comment_replay_id") Long commentReplayId
    ){
        return ResponseHandler.generateResponse("replay deleted successfully",
                HttpStatus.OK,
                commentsReplayService.deleteReplayOnComment(servletRequest,commentReplayId)
        );
    }

    @GetMapping("/get/all")
    public ResponseEntity<?> allReplayOnComment(HttpServletRequest servletRequest,
                                                   @PathVariable("comment_id") Long commentId
    ){
        return ResponseHandler.generateResponse("replay deleted successfully",
                HttpStatus.OK,
                commentsReplayService.deleteReplayOnComment(servletRequest,commentId)
        );
    }

}
