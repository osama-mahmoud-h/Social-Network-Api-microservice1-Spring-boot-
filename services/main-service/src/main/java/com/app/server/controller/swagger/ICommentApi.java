package com.app.server.controller.swagger;

import com.app.server.dto.request.comment.AddNewCommentRequestDto;
import com.app.server.dto.request.comment.GetAllCommentRepliesRequestDto;
import com.app.server.dto.request.comment.GetAllCommentsRequestDto;
import com.app.server.dto.request.comment.UpdateCommentRequestDto;
import com.app.server.dto.response.comment.CommentResponseDto;
import com.app.shared.security.dto.MyApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Set;

@Tag(name = "Comments", description = "APIs for managing comments on posts")
@SecurityRequirement(name = "jwtAuth")
public interface ICommentApi {

    @Operation(summary = "Add a new comment", description = "Add a new comment to a post")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comment added successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    ResponseEntity<MyApiResponse<Boolean>> writeComment(
            @RequestBody AddNewCommentRequestDto commentDto);

    @Operation(summary = "Delete a comment", description = "Delete a comment by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comment deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - not comment owner")
    })
    ResponseEntity<MyApiResponse<Boolean>> deleteComment(
            @Parameter(description = "Comment ID to delete") @PathVariable("comment_id") Long commentId);

    @Operation(summary = "Update a comment", description = "Update comment content")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comment updated successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    ResponseEntity<MyApiResponse<Boolean>> updateComment(
            @RequestBody UpdateCommentRequestDto updateCommentRequestDto);

    @Operation(summary = "Get all comments on a post", description = "Retrieve all comments for a specific post")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comments retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    ResponseEntity<MyApiResponse<Set<CommentResponseDto>>> allCommentsOnPost(
            @ModelAttribute GetAllCommentsRequestDto getAllCommentsRequestDto);

    @Operation(summary = "Reply to a comment", description = "Add a reply to an existing comment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reply added successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    ResponseEntity<MyApiResponse<?>> replayOnComment(
            @AuthenticationPrincipal UserDetails currentUserDetails,
            @RequestBody AddNewCommentRequestDto addNewCommentRequestDto,
            @Parameter(description = "Comment ID to reply to") @PathVariable("comment_id") Long commentId);

    @Operation(summary = "Get all replies on a comment", description = "Retrieve all replies for a specific comment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Replies retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    ResponseEntity<MyApiResponse<?>> allRepliesOnComment(
            @ModelAttribute GetAllCommentRepliesRequestDto getAllReplies);
}