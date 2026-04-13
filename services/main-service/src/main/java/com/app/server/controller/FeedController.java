package com.app.server.controller;

import com.app.server.dto.response.FeedResponseDto;
import com.app.server.feed.FeedService;
import com.app.shared.security.dto.MyApiResponse;
import com.app.shared.security.utils.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/feed")
@Tag(name = "News Feed", description = "Personalized news feed — shows posts authored by friends and posts friends reacted to or commented on")
@SecurityRequirement(name = "jwtAuth")
public class FeedController {

    private final FeedService feedService;

    @Operation(
        summary = "Get personalized news feed",
        description = "Returns a cursor-paginated feed of posts from friends and posts friends interacted with. " +
                      "Pass the `nextCursor` from the previous response as `cursor` to load the next page. " +
                      "Omit `cursor` on the first request to start from the most recent posts."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Feed retrieved successfully",
            content = @Content(schema = @Schema(implementation = MyApiResponse.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized — JWT token missing or invalid",
            content = @Content(schema = @Schema(implementation = MyApiResponse.class)))
    })
    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<MyApiResponse<FeedResponseDto>> getFeed(
            @Parameter(description = "Epoch milliseconds of the last item from the previous page. Omit for the first request.")
            @RequestParam(required = false) Long cursor,

            @Parameter(description = "Number of posts to return per page (default 20)")
            @RequestParam(defaultValue = "20") int size
    ) {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(MyApiResponse.success("Feed retrieved successfully",
                feedService.getFeed(userId, cursor, size)));
    }
}