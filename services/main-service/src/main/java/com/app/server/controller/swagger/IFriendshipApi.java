package com.app.server.controller.swagger;

import com.app.server.dto.response.AppUserResponseDto;
import com.app.shared.security.dto.MyApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Set;

@Tag(name = "Friendship", description = "APIs for managing friendships and friend requests")
@SecurityRequirement(name = "jwtAuth")
public interface IFriendshipApi {

    @Operation(summary = "Send friend request", description = "Send a friend request to another user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Friend request sent successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    ResponseEntity<MyApiResponse<Boolean>> addFriend(
            @Parameter(description = "User ID to send friend request to") @PathVariable("friend_id") Long friendId);

    @Operation(summary = "Remove friend", description = "Remove a friend from your friend list")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Friend removed successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    ResponseEntity<MyApiResponse<Boolean>> removeFriend(
            @Parameter(description = "Friend ID to remove") @PathVariable("friend_id") Long friendId);

    @Operation(summary = "Accept friend request", description = "Accept a pending friend request")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Friend request accepted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    ResponseEntity<MyApiResponse<Boolean>> acceptFriend(
            @Parameter(description = "User ID whose friend request to accept") @PathVariable("friend_id") Long friendId);

    @Operation(summary = "Cancel friend request", description = "Cancel a sent friend request")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Friend request cancelled successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    ResponseEntity<MyApiResponse<Boolean>> cancelFriendRequest(
            @Parameter(description = "User ID to cancel friend request for") @PathVariable("friend_id") Long friendId);

    @Operation(summary = "Block friend", description = "Block a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Friend blocked successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    ResponseEntity<MyApiResponse<Boolean>> blockFriend(
            @Parameter(description = "User ID to block") @PathVariable("friend_id") Long friendId);

    @Operation(summary = "Unblock friend", description = "Unblock a previously blocked user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Friend unblocked successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    ResponseEntity<MyApiResponse<Boolean>> unblockFriend(
            @Parameter(description = "User ID to unblock") @PathVariable("friend_id") Long friendId);

    @Operation(summary = "Get friends", description = "Retrieve list of all friends")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Friends retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    ResponseEntity<MyApiResponse<Set<AppUserResponseDto>>> getFriends();

    @Operation(summary = "Get friend requests", description = "Retrieve list of pending friend requests")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Friend requests retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    ResponseEntity<MyApiResponse<Set<AppUserResponseDto>>> getFriendRequests();

    @Operation(summary = "Get mutual friends count", description = "Get count of mutual friends with another user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Mutual friends count retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    ResponseEntity<MyApiResponse<Integer>> getMutualFriendsCount(
            @Parameter(description = "User ID to check mutual friends with") @PathVariable("friend_id") Long friendId);

    @Operation(summary = "Get mutual friends", description = "Retrieve list of mutual friends with another user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Mutual friends retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    ResponseEntity<MyApiResponse<Set<AppUserResponseDto>>> getMutualFriends(
            @Parameter(description = "User ID to get mutual friends with") @PathVariable("friend_id") Long friendId);

    @Operation(summary = "Suggest friends", description = "Get friend suggestions based on mutual connections")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Suggested friends retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    ResponseEntity<MyApiResponse<Set<AppUserResponseDto>>> suggestFriends();

    @Operation(summary = "Get friends paginated", description = "Retrieve paginated list of friends for chat")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Friends retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    ResponseEntity<MyApiResponse<Page<AppUserResponseDto>>> getFriendsPaginated(
            @Parameter(description = "Page number (0-indexed)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size);
}