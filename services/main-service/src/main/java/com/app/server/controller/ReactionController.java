package com.app.server.controller;

import com.app.server.dto.request.reaction.ReactToEntityRequestDto;
import com.app.server.model.UserProfile;
import com.app.server.service.ReactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Reactions", description = "APIs for reactions on posts and comments")
@RequiredArgsConstructor
@SecurityRequirement(name = "jwtAuth")
public class ReactionController {

    private final ReactionService reactionService;

    @Operation(summary = "React to a post", description = "Add, update or remove a reaction on a post")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reaction updated successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "Post not found")
    })
    @PutMapping("/react/post/{post_id}")
    public ResponseEntity<?> reactToPost(
            @AuthenticationPrincipal UserDetails currentUserDetails,
            @RequestBody ReactToEntityRequestDto reactToEntityRequestDto,
            @Parameter(description = "Post ID to react to") @PathVariable("post_id") Long postId
    ){
        return ResponseEntity.ok(reactionService.reactToPost((UserProfile) currentUserDetails, reactToEntityRequestDto, postId));
    }

    @Operation(summary = "React to a comment", description = "Add, update or remove a reaction on a comment")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reaction updated successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "Comment not found")
    })
    @PutMapping("/react/comment/{comment_id}")
    public ResponseEntity<?> reactToComment(
            @AuthenticationPrincipal UserDetails currentUserDetails,
            @RequestBody ReactToEntityRequestDto reactToEntityRequestDto,
            @Parameter(description = "Comment ID to react to") @PathVariable("comment_id") Long commentId
    ){
        return ResponseEntity.ok(reactionService.reactToComment((UserProfile) currentUserDetails, reactToEntityRequestDto, commentId));
    }
}
