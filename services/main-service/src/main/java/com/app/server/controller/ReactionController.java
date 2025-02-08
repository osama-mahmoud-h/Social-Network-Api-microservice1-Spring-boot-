package com.app.server.controller;

import com.app.server.dto.request.reaction.ReactToEntityRequestDto;
import com.app.server.model.AppUser;
import com.app.server.service.ReactionService;
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
@Tag(name = "Reaction Controller", description = "APIs for reactions on posts and comments")
@RequiredArgsConstructor
public class ReactionController {

    private final ReactionService reactionService;

    @PutMapping("/react/post/{post_id}")
    public ResponseEntity<?> reactToPost(
            @AuthenticationPrincipal UserDetails currentUserDetails,
            @RequestBody ReactToEntityRequestDto reactToEntityRequestDto,
            @PathVariable("post_id") Long postId
    ){
        return ResponseEntity.ok(reactionService.reactToPost((AppUser) currentUserDetails, reactToEntityRequestDto, postId));
    }

    @PutMapping("/react/comment/{comment_id}")
    public ResponseEntity<?> reactToComment(
            @AuthenticationPrincipal UserDetails currentUserDetails,
            @RequestBody ReactToEntityRequestDto reactToEntityRequestDto,
            @PathVariable("comment_id") Long commentId
    ){
        return ResponseEntity.ok(reactionService.reactToComment((AppUser) currentUserDetails, reactToEntityRequestDto, commentId));
    }
}
