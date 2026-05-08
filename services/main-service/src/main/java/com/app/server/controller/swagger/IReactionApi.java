package com.app.server.controller.swagger;

import com.app.server.dto.request.reaction.ReactToEntityRequestDto;
import com.app.shared.security.dto.MyApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Reactions", description = "APIs for reactions on posts and comments")
@SecurityRequirement(name = "jwtAuth")
public interface IReactionApi {

    @Operation(summary = "React to a post", description = "Add, update or remove a reaction on a post")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reaction updated successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Post not found")
    })
    ResponseEntity<MyApiResponse<Boolean>> reactToPost(
            @RequestBody ReactToEntityRequestDto reactToEntityRequestDto,
            @Parameter(description = "Post ID to react to") @PathVariable("post_id") Long postId);

    @Operation(summary = "React to a comment", description = "Add, update or remove a reaction on a comment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reaction updated successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Comment not found")
    })
    ResponseEntity<MyApiResponse<Boolean>> reactToComment(
            @RequestBody ReactToEntityRequestDto reactToEntityRequestDto,
            @Parameter(description = "Comment ID to react to") @PathVariable("comment_id") Long commentId);
}