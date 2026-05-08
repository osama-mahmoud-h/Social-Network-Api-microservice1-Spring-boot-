package com.app.server.controller.swagger;

import com.app.server.dto.response.FeedResponseDto;
import com.app.shared.security.dto.MyApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "News Feed", description = "Personalized news feed — shows posts authored by friends and posts friends reacted to or commented on")
@SecurityRequirement(name = "jwtAuth")
public interface IFeedApi {

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
    ResponseEntity<MyApiResponse<FeedResponseDto>> getFeed(
            @Parameter(description = "Epoch milliseconds of the last item from the previous page. Omit for the first request.")
            @RequestParam(required = false) Long cursor,

            @Parameter(description = "Number of posts to return per page (default 20)")
            @RequestParam(defaultValue = "20") int size);
}