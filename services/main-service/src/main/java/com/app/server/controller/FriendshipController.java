package com.app.server.controller;


import com.app.server.controller.swagger.IFriendshipApi;
import com.app.server.dto.response.AppUserResponseDto;
import com.app.server.model.UserProfile;
import com.app.server.repository.UserProfileRepository;
import com.app.server.service.FriendshipService;
import com.app.shared.security.dto.MyApiResponse;
import com.app.shared.security.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/friendship")
public class FriendshipController implements IFriendshipApi {

    private final FriendshipService friendshipService;
    private final UserProfileRepository userProfileRepository;

    private UserProfile getCurrentUserProfile() {
        Long userId = SecurityUtils.getCurrentUserId();
        return userProfileRepository.findUserByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User profile not found for userId: " + userId));
    }

    @Override
    @PostMapping("/add-friend/{friend_id}")
    public ResponseEntity<MyApiResponse<Boolean>> addFriend(
            @PathVariable("friend_id") Long friendId
    ) {
        UserProfile currentUser = getCurrentUserProfile();
        boolean isAdded = friendshipService.addFriend(currentUser, friendId);
        return ResponseEntity.ok(MyApiResponse.success("Friend request sent", isAdded));
    }

    @Override
    @DeleteMapping("/remove-friend/{friend_id}")
    public ResponseEntity<MyApiResponse<Boolean>> removeFriend(
            @PathVariable("friend_id") Long friendId
    ) {
        UserProfile currentUser = getCurrentUserProfile();
        boolean isRemoved = friendshipService.removeFriend(currentUser, friendId);
        return ResponseEntity.ok(MyApiResponse.success( "Friend removed",isRemoved));
    }

    @Override
    @PutMapping("/accept-friend/{friend_id}")
    public ResponseEntity<MyApiResponse<Boolean>> acceptFriend(
            @PathVariable("friend_id") Long friendId
    ) {
        UserProfile currentUser = getCurrentUserProfile();
        boolean isAccepted = friendshipService.acceptFriend(currentUser, friendId);
        return ResponseEntity.ok(MyApiResponse.success( "Friend request accepted", isAccepted));
    }

    @Override
    @DeleteMapping("/cancel-friend-request/{friend_id}")
    public ResponseEntity<MyApiResponse<Boolean>> cancelFriendRequest(
            @PathVariable("friend_id") Long friendId
    ) {
        UserProfile currentUser = getCurrentUserProfile();
        boolean isCancelled = friendshipService.cancelFriendRequest(currentUser, friendId);
        return ResponseEntity.ok(MyApiResponse.success("Friend request cancelled",isCancelled));
    }

    @Override
    @PutMapping("/block-friend/{friend_id}")
    public ResponseEntity<MyApiResponse<Boolean>> blockFriend(
            @PathVariable("friend_id") Long friendId
    ) {
        UserProfile currentUser = getCurrentUserProfile();
        boolean isBlocked = friendshipService.blockFriend(currentUser, friendId);
        return ResponseEntity.ok(MyApiResponse.success( "Friend blocked",isBlocked));
    }

    @Override
    @PutMapping("/unblock-friend/{friend_id}")
    public ResponseEntity<MyApiResponse<Boolean>> unblockFriend(
            @PathVariable("friend_id") Long friendId
    ) {
        UserProfile currentUser = getCurrentUserProfile();
        boolean isUnblocked = friendshipService.unblockFriend(currentUser, friendId);
        return ResponseEntity.ok(MyApiResponse.success( "Friend unblocked",isUnblocked));
    }

    @Override
    @GetMapping("/get-friends")
    public ResponseEntity<MyApiResponse<Set<AppUserResponseDto>>> getFriends() {
        UserProfile currentUser = getCurrentUserProfile();
        log.info("Getting friends for user: {}", currentUser.getEmail());
        Set<AppUserResponseDto> friends = friendshipService.getFriends(currentUser);
        return ResponseEntity.ok(MyApiResponse.success( "Friends retrieved",friends));
    }

    @Override
    @GetMapping("/get-friend-requests")
    public ResponseEntity<MyApiResponse<Set<AppUserResponseDto>>> getFriendRequests() {
        UserProfile currentUser = getCurrentUserProfile();
        Set<AppUserResponseDto> friendRequests = friendshipService.getFriendRequests(currentUser);
        return ResponseEntity.ok(MyApiResponse.success( "Friend requests retrieved",friendRequests));
    }

    @Override
    @GetMapping("/get-mutual-friends-count/{friend_id}")
    public ResponseEntity<MyApiResponse<Integer>> getMutualFriendsCount(
            @PathVariable("friend_id") Long friendId
    ) {
        UserProfile currentUser = getCurrentUserProfile();
        int mutualFriendsCount = friendshipService.getMutualFriendsCount(currentUser, friendId);
        return ResponseEntity.ok(MyApiResponse.success( "Mutual friends count retrieved",mutualFriendsCount));
    }

    @Override
    @GetMapping("/get-mutual-friends/{friend_id}")
    public ResponseEntity<MyApiResponse<Set<AppUserResponseDto>>> getMutualFriends(
            @PathVariable("friend_id") Long friendId
    ) {
        UserProfile currentUser = getCurrentUserProfile();
        Set<AppUserResponseDto> mutualFriends = friendshipService.getMutualFriends(currentUser, friendId);
        return ResponseEntity.ok(MyApiResponse.success( "Mutual friends retrieved",mutualFriends));
    }

    @Override
    @GetMapping("/suggest-friends")
    public ResponseEntity<MyApiResponse<Set<AppUserResponseDto>>> suggestFriends(
    ) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        Set<AppUserResponseDto> suggestedFriends = friendshipService.suggestFriends(currentUserId);
        return ResponseEntity.ok(MyApiResponse.success("Suggested friends retrieved", suggestedFriends));
    }

    @Override
    @GetMapping("/get-friends-paginated")
    public ResponseEntity<MyApiResponse<Page<AppUserResponseDto>>> getFriendsPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        log.info("Fetching paginated friends list: userId={} page={}, size={}", currentUserId, page, size);
        Page<AppUserResponseDto> friends = friendshipService.getFriendsPaginated(currentUserId, page, size);

        log.info("Returning {} friends for page {}", friends.getContent(), page);
        return ResponseEntity.ok(MyApiResponse.success( "Friends retrieved",friends));
    }


}