package com.app.server.controller;

import com.app.server.controller.swagger.ICommentApi;
import com.app.server.dto.request.comment.AddNewCommentRequestDto;
import com.app.server.dto.request.comment.GetAllCommentRepliesRequestDto;
import com.app.server.dto.request.comment.GetAllCommentsRequestDto;
import com.app.server.dto.request.comment.UpdateCommentRequestDto;
import com.app.server.dto.response.comment.CommentResponseDto;
import com.app.server.model.UserProfile;
import com.app.server.repository.UserProfileRepository;
import com.app.server.service.CommentService;
import com.app.shared.security.dto.MyApiResponse;
import com.app.shared.security.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/comment")
public class CommentController implements ICommentApi {
    private final CommentService commentService;
    private final UserProfileRepository userProfileRepository;

    private UserProfile getCurrentUserProfile() {
        Long userId = SecurityUtils.getCurrentUserId();
        return userProfileRepository.findUserByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User profile not found for userId: " + userId));
    }

    @Override
    @PostMapping("/add-new")
    public ResponseEntity<MyApiResponse<Boolean>> writeComment(
            @RequestBody AddNewCommentRequestDto commentDto
    ) {
        UserProfile currentUserProfile = getCurrentUserProfile();

        return ResponseEntity.ok(
                MyApiResponse.success("Comment added successfully",
                        commentService.addNewComment(currentUserProfile, commentDto))
        );
    }

    @Override
    @DeleteMapping ("/delete/{comment_id}")
    public ResponseEntity<MyApiResponse<Boolean>> deleteComment(
            @PathVariable("comment_id") Long commentId
    ){
        UserProfile currentUserProfile = getCurrentUserProfile();
        return ResponseEntity.ok(
                MyApiResponse.success("Comment deleted successfully",
                        commentService.deleteComment(currentUserProfile, commentId))
        );
    }

    @Override
    @PutMapping("/update")
    public ResponseEntity<MyApiResponse<Boolean>> updateComment(
            @RequestBody UpdateCommentRequestDto updateCommentRequestDto
            ){
        UserProfile currentUserProfile = getCurrentUserProfile();
        boolean updated = commentService.updateComment(currentUserProfile, updateCommentRequestDto);
        return ResponseEntity.ok(MyApiResponse.success("Comment updated successfully", updated));
    }

    @Override
    @GetMapping("/all")
    public ResponseEntity<MyApiResponse<Set<CommentResponseDto>>> allCommentsOnPost(
            @ModelAttribute GetAllCommentsRequestDto getAllCommentsRequestDto
            ){
        UserProfile currentUserProfile = getCurrentUserProfile();
        Set<CommentResponseDto> retrievedComments = commentService.getCommentsOnPost(currentUserProfile, getAllCommentsRequestDto);
        return ResponseEntity.ok(MyApiResponse.success("All comments fetched successfully", retrievedComments));
    }

    @Override
    @PostMapping("/replay/{comment_id}")
    public ResponseEntity<MyApiResponse<?>> replayOnComment(
            @AuthenticationPrincipal UserDetails currentUserDetails,
            @RequestBody AddNewCommentRequestDto addNewCommentRequestDto,
            @PathVariable("comment_id") Long commentId
            ){
        UserProfile currentUserProfile = getCurrentUserProfile();

        Boolean replayed = commentService.replayOnComment(currentUserProfile,addNewCommentRequestDto, commentId);
        return ResponseEntity.ok(MyApiResponse.success("Reply added successfully", replayed));
    }

    @Override
    @GetMapping("/replies/all")
    public ResponseEntity<MyApiResponse<?>> allRepliesOnComment(
            @ModelAttribute GetAllCommentRepliesRequestDto getAllReplies
            ){
        UserProfile currentUserProfile = getCurrentUserProfile();
        Set<CommentResponseDto> retrievedReplies= commentService.getCommentReplies(currentUserProfile, getAllReplies);
        return ResponseEntity.ok(MyApiResponse.success("All replies fetched successfully", retrievedReplies));
    }


}