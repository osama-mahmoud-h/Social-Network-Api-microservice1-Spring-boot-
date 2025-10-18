package com.app.server.controller;


import com.app.server.dto.response.AppUserResponseDto;
import com.app.server.dto.response.MyApiResponse;
import com.app.server.model.UserProfile;
import com.app.server.service.FriendshipService;
import com.app.shared.security.utils.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@Slf4j
@RestController
@Tag(name = "Friendship", description = "APIs for managing friendships and friend requests")
@RequiredArgsConstructor
@RequestMapping("/api/v1/friendship")
@SecurityRequirement(name = "jwtAuth")
public class FriendshipController {

    private final FriendshipService friendshipService;

    @Operation(summary = "Send friend request", description = "Send a friend request to another user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Friend request sent successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping("/add-friend/{friend_id}")
    public ResponseEntity<MyApiResponse<Boolean>> addFriend(
            @AuthenticationPrincipal UserDetails currentUserDetails,
            @Parameter(description = "User ID to send friend request to") @PathVariable("friend_id") Long friendId
    ) {
        boolean isAdded = friendshipService.addFriend((UserProfile) currentUserDetails, friendId);
        return ResponseEntity.ok(MyApiResponse.success(isAdded, "Friend request sent"));
    }

    @Operation(summary = "Remove friend", description = "Remove a friend from your friend list")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Friend removed successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @DeleteMapping("/remove-friend/{friend_id}")
    public ResponseEntity<MyApiResponse<Boolean>> removeFriend(
            @AuthenticationPrincipal UserDetails currentUserDetails,
            @Parameter(description = "Friend ID to remove") @PathVariable("friend_id") Long friendId
    ) {
        boolean isRemoved = friendshipService.removeFriend((UserProfile) currentUserDetails, friendId);
        return ResponseEntity.ok(MyApiResponse.success(isRemoved, "Friend removed"));
    }

    @Operation(summary = "Accept friend request", description = "Accept a pending friend request")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Friend request accepted successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PutMapping("/accept-friend/{friend_id}")
    public ResponseEntity<MyApiResponse<Boolean>> acceptFriend(
            @AuthenticationPrincipal UserDetails currentUserDetails,
            @Parameter(description = "User ID whose friend request to accept") @PathVariable("friend_id") Long friendId
    ) {
        boolean isAccepted = friendshipService.acceptFriend((UserProfile) currentUserDetails, friendId);
        return ResponseEntity.ok(MyApiResponse.success(isAccepted, "Friend request accepted"));
    }

    @Operation(summary = "Cancel friend request", description = "Cancel a sent friend request")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Friend request cancelled successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @DeleteMapping("/cancel-friend-request/{friend_id}")
    public ResponseEntity<MyApiResponse<Boolean>> cancelFriendRequest(
            @AuthenticationPrincipal UserDetails currentUserDetails,
            @Parameter(description = "User ID to cancel friend request for") @PathVariable("friend_id") Long friendId
    ) {
        boolean isCancelled = friendshipService.cancelFriendRequest((UserProfile) currentUserDetails, friendId);
        return ResponseEntity.ok(MyApiResponse.success(isCancelled, "Friend request cancelled"));
    }

    @Operation(summary = "Block friend", description = "Block a user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Friend blocked successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PutMapping("/block-friend/{friend_id}")
    public ResponseEntity<MyApiResponse<Boolean>> blockFriend(
            @AuthenticationPrincipal UserDetails currentUserDetails,
            @Parameter(description = "User ID to block") @PathVariable("friend_id") Long friendId
    ) {
        boolean isBlocked = friendshipService.blockFriend((UserProfile) currentUserDetails, friendId);
        return ResponseEntity.ok(MyApiResponse.success(isBlocked, "Friend blocked"));
    }

    @Operation(summary = "Unblock friend", description = "Unblock a previously blocked user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Friend unblocked successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PutMapping("/unblock-friend/{friend_id}")
    public ResponseEntity<MyApiResponse<Boolean>> unblockFriend(
            @AuthenticationPrincipal UserDetails currentUserDetails,
            @Parameter(description = "User ID to unblock") @PathVariable("friend_id") Long friendId
    ) {
        boolean isUnblocked = friendshipService.unblockFriend((UserProfile) currentUserDetails, friendId);
        return ResponseEntity.ok(MyApiResponse.success(isUnblocked, "Friend unblocked"));
    }

    @Operation(summary = "Get friends", description = "Retrieve list of all friends")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Friends retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/get-friends")
    public ResponseEntity<MyApiResponse<Set<AppUserResponseDto>>> getFriends(
            @AuthenticationPrincipal UserDetails currentUserDetails
    ) {
        Set<AppUserResponseDto> friends = friendshipService.getFriends((UserProfile) currentUserDetails);
        return ResponseEntity.ok(MyApiResponse.success(friends, "Friends retrieved"));
    }

    @Operation(summary = "Get friend requests", description = "Retrieve list of pending friend requests")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Friend requests retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/get-friend-requests")
    public ResponseEntity<MyApiResponse<Set<AppUserResponseDto>>> getFriendRequests(
            @AuthenticationPrincipal UserDetails currentUserDetails
    ) {
        Set<AppUserResponseDto> friendRequests = friendshipService.getFriendRequests((UserProfile) currentUserDetails);
        return ResponseEntity.ok(MyApiResponse.success(friendRequests, "Friend requests retrieved"));
    }

    @Operation(summary = "Get mutual friends count", description = "Get count of mutual friends with another user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Mutual friends count retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/get-mutual-friends-count/{friend_id}")
    public ResponseEntity<MyApiResponse<Integer>> getMutualFriendsCount(
            @AuthenticationPrincipal UserDetails currentUserDetails,
            @Parameter(description = "User ID to check mutual friends with") @PathVariable("friend_id") Long friendId
    ) {
        int mutualFriendsCount = friendshipService.getMutualFriendsCount((UserProfile) currentUserDetails, friendId);
        return ResponseEntity.ok(MyApiResponse.success(mutualFriendsCount, "Mutual friends count retrieved"));
    }

    @Operation(summary = "Get mutual friends", description = "Retrieve list of mutual friends with another user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Mutual friends retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/get-mutual-friends/{friend_id}")
    public ResponseEntity<MyApiResponse<Set<AppUserResponseDto>>> getMutualFriends(
            @AuthenticationPrincipal UserDetails currentUserDetails,
            @Parameter(description = "User ID to get mutual friends with") @PathVariable("friend_id") Long friendId
    ) {
        Set<AppUserResponseDto> mutualFriends = friendshipService.getMutualFriends((UserProfile) currentUserDetails, friendId);
        return ResponseEntity.ok(MyApiResponse.success(mutualFriends, "Mutual friends retrieved"));
    }

    @Operation(summary = "Suggest friends", description = "Get friend suggestions based on mutual connections")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Suggested friends retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/suggest-friends")
    public ResponseEntity<MyApiResponse<Set<AppUserResponseDto>>> suggestFriends(
    ) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        Set<AppUserResponseDto> suggestedFriends = friendshipService.suggestFriends(currentUserId);
        return ResponseEntity.ok(MyApiResponse.success(suggestedFriends, "Suggested friends retrieved"));
    }

    @Operation(summary = "Get friends paginated", description = "Retrieve paginated list of friends for chat")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Friends retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/get-friends-paginated")
    public ResponseEntity<MyApiResponse<Page<AppUserResponseDto>>> getFriendsPaginated(
            @Parameter(description = "Page number (0-indexed)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size
    ) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        log.info("Fetching paginated friends list: userId={} page={}, size={}", currentUserId, page, size);
        Page<AppUserResponseDto> friends = friendshipService.getFriendsPaginated(currentUserId, page, size);

        log.info("Returning {} friends for page {}", friends.getContent(), page);
        return ResponseEntity.ok(MyApiResponse.success(friends, "Friends retrieved"));
    }


}
