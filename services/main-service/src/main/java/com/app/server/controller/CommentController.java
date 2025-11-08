package com.app.server.controller;

import com.app.server.dto.request.comment.AddNewCommentRequestDto;
import com.app.server.dto.request.comment.GetAllCommentRepliesRequestDto;
import com.app.server.dto.request.comment.GetAllCommentsRequestDto;
import com.app.server.dto.request.comment.UpdateCommentRequestDto;
import com.app.server.repository.UserProfileRepository;
import com.app.shared.security.dto.MyApiResponse;
import com.app.server.dto.response.comment.CommentResponseDto;
import com.app.server.model.UserProfile;
import com.app.server.service.CommentService;
import com.app.shared.security.utils.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/comment")
@Tag(name = "Comments", description = "APIs for managing comments on posts")
@SecurityRequirement(name = "jwtAuth")
public class CommentController {
    private final CommentService commentService;
    private final UserProfileRepository userProfileRepository;

    private UserProfile getCurrentUserProfile() {
        Long userId = SecurityUtils.getCurrentUserId();
        return userProfileRepository.findUserByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User profile not found for userId: " + userId));
    }
    @Operation(summary = "Add a new comment", description = "Add a new comment to a post")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Comment added successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping("/add-new")
    public ResponseEntity<MyApiResponse<Boolean>> writeComment(
            @RequestBody AddNewCommentRequestDto commentDto
    ) {
        UserProfile currentUserProfile = getCurrentUserProfile();
        System.out.println();
        return ResponseEntity.ok(
                MyApiResponse.success("Comment added successfully",
                        commentService.addNewComment(currentUserProfile, commentDto))
        );
    }

    @Operation(summary = "Delete a comment", description = "Delete a comment by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Comment deleted successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden - not comment owner")
    })
    @DeleteMapping ("/delete/{comment_id}")
    public ResponseEntity<MyApiResponse<Boolean>> deleteComment(
            @Parameter(description = "Comment ID to delete") @PathVariable("comment_id") Long commentId
    ){
        UserProfile currentUserProfile = getCurrentUserProfile();
        return ResponseEntity.ok(
                MyApiResponse.success("Comment deleted successfully",
                        commentService.deleteComment(currentUserProfile, commentId))
        );
    }

    @Operation(summary = "Update a comment", description = "Update comment content")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Comment updated successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PutMapping("/update")
    public ResponseEntity<MyApiResponse<Boolean>> updateComment(
            @RequestBody UpdateCommentRequestDto updateCommentRequestDto
            ){
        UserProfile currentUserProfile = getCurrentUserProfile();
        boolean updated = commentService.updateComment(currentUserProfile, updateCommentRequestDto);
        return ResponseEntity.ok(MyApiResponse.success("Comment updated successfully", updated));
    }

    @Operation(summary = "Get all comments on a post", description = "Retrieve all comments for a specific post")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Comments retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/all")
    public ResponseEntity<MyApiResponse<Set<CommentResponseDto>>> allCommentsOnPost(
            @ModelAttribute GetAllCommentsRequestDto getAllCommentsRequestDto
            ){
        UserProfile currentUserProfile = getCurrentUserProfile();
        Set<CommentResponseDto> retrievedComments = commentService.getCommentsOnPost(currentUserProfile, getAllCommentsRequestDto);
        return ResponseEntity.ok(MyApiResponse.success("All comments fetched successfully", retrievedComments));
    }

    @Operation(summary = "Reply to a comment", description = "Add a reply to an existing comment")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reply added successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
     @PostMapping("/replay/{comment_id}")
     public ResponseEntity<MyApiResponse<?>> replayOnComment(
             @AuthenticationPrincipal UserDetails currentUserDetails,
             @RequestBody AddNewCommentRequestDto addNewCommentRequestDto,
             @Parameter(description = "Comment ID to reply to") @PathVariable("comment_id") Long commentId
             ){
        UserProfile currentUserProfile = getCurrentUserProfile();

        Boolean replayed = commentService.replayOnComment(currentUserProfile,addNewCommentRequestDto, commentId);
         return ResponseEntity.ok(MyApiResponse.success("Reply added successfully", replayed));
     }

    @Operation(summary = "Get all replies on a comment", description = "Retrieve all replies for a specific comment")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Replies retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
     @GetMapping("/replies/all")
     public ResponseEntity<MyApiResponse<?>> allRepliesOnComment(
             @ModelAttribute GetAllCommentRepliesRequestDto getAllReplies
             ){
        UserProfile currentUserProfile = getCurrentUserProfile();
         Set<CommentResponseDto> retrievedReplies= commentService.getCommentReplies(currentUserProfile, getAllReplies);
         return ResponseEntity.ok(MyApiResponse.success("All replies fetched successfully", retrievedReplies));
     }
    

}
