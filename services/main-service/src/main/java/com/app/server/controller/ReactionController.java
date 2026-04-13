package com.app.server.controller;

import com.app.server.dto.request.reaction.ReactToEntityRequestDto;
import com.app.shared.security.dto.MyApiResponse;
import com.app.server.model.UserProfile;
import com.app.server.service.ReactionService;
import com.app.shared.security.utils.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;


@Slf4j
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
    public ResponseEntity<MyApiResponse<Boolean>> reactToPost(
            @RequestBody ReactToEntityRequestDto reactToEntityRequestDto,
            @Parameter(description = "Post ID to react to") @PathVariable("post_id") Long postId
    ){
        Long currentUserId = SecurityUtils.getCurrentUserId();
        log.debug("Received request to react to post with ID: {} , by user: {}", postId, currentUserId);
        Boolean reacted = reactionService.reactToPost(currentUserId, reactToEntityRequestDto, postId);
        return ResponseEntity.ok(MyApiResponse.success("Reaction updated successfully", reacted));
    }

    @Operation(summary = "React to a comment", description = "Add, update or remove a reaction on a comment")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reaction updated successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "Comment not found")
    })
    @PutMapping("/react/comment/{comment_id}")
    public ResponseEntity<MyApiResponse<Boolean>> reactToComment(
            @RequestBody ReactToEntityRequestDto reactToEntityRequestDto,
            @Parameter(description = "Comment ID to react to") @PathVariable("comment_id") Long commentId
    ){
        Long currentUserId = SecurityUtils.getCurrentUserId();
        Boolean reacted = reactionService.reactToComment(currentUserId, reactToEntityRequestDto, commentId);
        return ResponseEntity.ok(MyApiResponse.success("Reaction updated successfully", reacted));
    }
}
