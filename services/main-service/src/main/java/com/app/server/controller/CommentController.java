package com.app.server.controller;

import com.app.server.dto.request.comment.AddNewCommentRequestDto;
import com.app.server.dto.request.comment.GetAllCommentsRequestDto;
import com.app.server.dto.request.comment.UpdateCommentRequestDto;
import com.app.server.dto.response.MyApiResponse;
import com.app.server.dto.response.comment.CommentResponseDto;
import com.app.server.model.AppUser;
import com.app.server.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/comment")
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/add-new")
    public ResponseEntity<?> writeComment(
            @AuthenticationPrincipal UserDetails currentUserDetails,
            @RequestBody AddNewCommentRequestDto commentDto
    ) {
        return ResponseEntity.ok(MyApiResponse.success(commentService.addNewComment((AppUser)currentUserDetails, commentDto),"comment added successfully"));
    }

    @DeleteMapping ("/delete/{comment_id}")
    public ResponseEntity<MyApiResponse<Boolean>> deleteComment(
            @AuthenticationPrincipal UserDetails currentUserDetails,
            @PathVariable("comment_id") Long commentId
    ){
        return ResponseEntity.ok(
                MyApiResponse.success(
                        commentService.deleteComment((AppUser)currentUserDetails,
                        commentId),"comment deleted successfully")
        );
    }

    @PutMapping("/update")
    public ResponseEntity<MyApiResponse<Boolean>> updateComment(
            @AuthenticationPrincipal UserDetails currentUserDetails,
            @RequestBody UpdateCommentRequestDto updateCommentRequestDto
            ){
        boolean updated = commentService.updateComment((AppUser)currentUserDetails, updateCommentRequestDto);
        return ResponseEntity.ok(MyApiResponse.success(updated,"comment updated successfully"));
    }

    @GetMapping("/all")
    public ResponseEntity<MyApiResponse<Set<CommentResponseDto>>> allCommentsOnPost(
            @AuthenticationPrincipal UserDetails currentUserDetails,
            @ModelAttribute GetAllCommentsRequestDto getAllCommentsRequestDto
            ){
        Set<CommentResponseDto> retrievedComments = commentService.getCommentsOnPost((AppUser)currentUserDetails, getAllCommentsRequestDto);
        return ResponseEntity.ok(MyApiResponse.success(retrievedComments,"all comments fetched successfully"));
    }

    // @GetMapping("/replay")
    // public ResponseEntity<MyApiResponse<Set<CommentResponseDto>> replayOnComment(
    //         @AuthenticationPrincipal UserDetails currentUserDetails,
    //         @ModelAttribute GetAllCommentsRequestDto getAllCommentsRequestDto
    //         ){
    //     Set<CommentResponseDto> retrievedComments = commentService.getReplayOnComment((AppUser)currentUserDetails, getAllCommentsRequestDto);
    //     return ResponseEntity.ok(MyApiResponse.success(retrievedComments,"all comments fetched successfully"));
    // }

    // @GetMapping("/replies/all")
    // public ResponseEntity<MyApiResponse<Set<CommentResponseDto>> allRepliesOnComment(
    //         @AuthenticationPrincipal UserDetails currentUserDetails,
    //         @ModelAttribute GetAllCommentsRequestDto getAllCommentsRequestDto
    //         ){
    //     Set<CommentResponseDto> retrievedComments = commentService.getAllRepliesOnComment((AppUser)currentUserDetails, getAllCommentsRequestDto);
    //     return ResponseEntity.ok(MyApiResponse.success(retrievedComments,"all comments fetched successfully"));
    // }
    

}
