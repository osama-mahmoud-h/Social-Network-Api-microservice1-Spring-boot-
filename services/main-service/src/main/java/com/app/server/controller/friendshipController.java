package com.app.server.controller;


import com.app.server.dto.response.AppUserResponseDto;
import com.app.server.dto.response.MyApiResponse;
import com.app.server.model.AppUser;
import com.app.server.service.FriendshipService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@Tag(name = "Friendship", description = "The Friendship API")
@RequiredArgsConstructor
@RequestMapping("/api/v1/friendship")
public class friendshipController {

    private final FriendshipService friendshipService;

    @PostMapping("/add-friend/{friend_id}")
    public ResponseEntity<MyApiResponse<Boolean>> addFriend(
            @AuthenticationPrincipal UserDetails currentUserDetails,
            @PathVariable("friend_id") Long friendId
    ) {
        boolean isAdded = friendshipService.addFriend((AppUser) currentUserDetails, friendId);
        return ResponseEntity.ok(MyApiResponse.success(isAdded, "Friend request sent"));
    }

    @DeleteMapping("/remove-friend/{friend_id}")
    public ResponseEntity<MyApiResponse<Boolean>> removeFriend(
            @AuthenticationPrincipal UserDetails currentUserDetails,
            @PathVariable("friend_id") Long friendId
    ) {
        boolean isRemoved = friendshipService.removeFriend((AppUser) currentUserDetails, friendId);
        return ResponseEntity.ok(MyApiResponse.success(isRemoved, "Friend removed"));
    }

    @PutMapping("/accept-friend/{friend_id}")
    public ResponseEntity<MyApiResponse<Boolean>> acceptFriend(
            @AuthenticationPrincipal UserDetails currentUserDetails,
            @PathVariable("friend_id") Long friendId
    ) {
        boolean isAccepted = friendshipService.acceptFriend((AppUser) currentUserDetails, friendId);
        return ResponseEntity.ok(MyApiResponse.success(isAccepted, "Friend request accepted"));
    }

    @DeleteMapping("/cancel-friend-request/{friend_id}")
    public ResponseEntity<MyApiResponse<Boolean>> cancelFriendRequest(
            @AuthenticationPrincipal UserDetails currentUserDetails,
            @PathVariable("friend_id") Long friendId
    ) {
        boolean isCancelled = friendshipService.cancelFriendRequest((AppUser) currentUserDetails, friendId);
        return ResponseEntity.ok(MyApiResponse.success(isCancelled, "Friend request cancelled"));
    }

    @PutMapping("/block-friend/{friend_id}")
    public ResponseEntity<MyApiResponse<Boolean>> blockFriend(
            @AuthenticationPrincipal UserDetails currentUserDetails,
            @PathVariable("friend_id") Long friendId
    ) {
        boolean isBlocked = friendshipService.blockFriend((AppUser) currentUserDetails, friendId);
        return ResponseEntity.ok(MyApiResponse.success(isBlocked, "Friend blocked"));
    }

    @PutMapping("/unblock-friend/{friend_id}")
    public ResponseEntity<MyApiResponse<Boolean>> unblockFriend(
            @AuthenticationPrincipal UserDetails currentUserDetails,
            @PathVariable("friend_id") Long friendId
    ) {
        boolean isUnblocked = friendshipService.unblockFriend((AppUser) currentUserDetails, friendId);
        return ResponseEntity.ok(MyApiResponse.success(isUnblocked, "Friend unblocked"));
    }

    @GetMapping("/get-friends")
    public ResponseEntity<MyApiResponse<Set<AppUserResponseDto>>> getFriends(
            @AuthenticationPrincipal UserDetails currentUserDetails
    ) {
        Set<AppUserResponseDto> friends = friendshipService.getFriends((AppUser) currentUserDetails);
        return ResponseEntity.ok(MyApiResponse.success(friends, "Friends retrieved"));
    }

    @GetMapping("/get-friend-requests")
    public ResponseEntity<MyApiResponse<Set<AppUserResponseDto>>> getFriendRequests(
            @AuthenticationPrincipal UserDetails currentUserDetails
    ) {
        Set<AppUserResponseDto> friendRequests = friendshipService.getFriendRequests((AppUser) currentUserDetails);
        return ResponseEntity.ok(MyApiResponse.success(friendRequests, "Friend requests retrieved"));
    }



}
