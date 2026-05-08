package com.app.server.controller;

import com.app.server.controller.swagger.IReactionApi;
import com.app.server.dto.request.reaction.ReactToEntityRequestDto;
import com.app.server.service.ReactionService;
import com.app.shared.security.dto.MyApiResponse;
import com.app.shared.security.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequiredArgsConstructor
public class ReactionController implements IReactionApi {

    private final ReactionService reactionService;

    @Override
    @PutMapping("/react/post/{post_id}")
    public ResponseEntity<MyApiResponse<Boolean>> reactToPost(
            @RequestBody ReactToEntityRequestDto reactToEntityRequestDto,
            @PathVariable("post_id") Long postId
    ){
        Long currentUserId = SecurityUtils.getCurrentUserId();
        log.debug("Received request to react to post with ID: {} , by user: {}", postId, currentUserId);
        Boolean reacted = reactionService.reactToPost(currentUserId, reactToEntityRequestDto, postId);
        return ResponseEntity.ok(MyApiResponse.success("Reaction updated successfully", reacted));
    }

    @Override
    @PutMapping("/react/comment/{comment_id}")
    public ResponseEntity<MyApiResponse<Boolean>> reactToComment(
            @RequestBody ReactToEntityRequestDto reactToEntityRequestDto,
            @PathVariable("comment_id") Long commentId
    ){
        Long currentUserId = SecurityUtils.getCurrentUserId();
        Boolean reacted = reactionService.reactToComment(currentUserId, reactToEntityRequestDto, commentId);
        return ResponseEntity.ok(MyApiResponse.success("Reaction updated successfully", reacted));
    }
}