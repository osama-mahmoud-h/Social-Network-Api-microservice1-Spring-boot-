package com.app.server.service.impl;

import com.app.server.dto.notification.NotificationEvent;
import com.app.server.dto.response.AppUserResponseDto;
import com.app.server.enums.FriendshipStatus;
import com.app.server.enums.NotificationType;
import com.app.server.exception.CustomRuntimeException;
import com.app.server.mapper.FriendshipMapper;
import com.app.server.mapper.UserMapper;
import com.app.server.model.AppUser;
import com.app.server.model.Friendship;
import com.app.server.repository.AppUserRepository;
import com.app.server.repository.FriendshipServiceRepository;
import com.app.server.service.FriendshipService;
import com.app.server.service.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FriendshipServiceImpl implements FriendshipService {

    private final FriendshipServiceRepository friendshipServiceRepository;
    private final AppUserRepository appUserRepository;
    private final FriendshipMapper friendshipMapper;
    private final UserMapper userMapper;
    private final NotificationService notificationService;

    @Override
    public boolean addFriend(AppUser currentUser, Long friendId) {
        AppUser friend = getUserById(friendId);
        Optional<Friendship> optionalFriendship = friendshipServiceRepository.findFriendshipByTwoUsers(currentUser.getUserId(), friendId);

        if (isMyFriend(currentUser, optionalFriendship)) {
            throw new CustomRuntimeException("You are already friends", HttpStatus.CONFLICT);
        }
        Friendship friendship = friendshipMapper.mapToFriendship(currentUser, friend);
        friendshipServiceRepository.save(friendship);
        this.sendFriendRequestNotification(currentUser, friend);
        return true;
    }

    @Override
    public boolean removeFriend(AppUser currentUser, Long friendId) {
        Friendship optionalFriendship = friendshipServiceRepository.findFriendshipByTwoUsers(currentUser.getUserId(), friendId)
                .orElseThrow(() -> new CustomRuntimeException("You are not friends", HttpStatus.NOT_FOUND));

        if(!isMyFriend(currentUser, Optional.of(optionalFriendship))) {
            throw new CustomRuntimeException("You are not friends", HttpStatus.NOT_FOUND);
        }
        friendshipServiceRepository.delete(optionalFriendship);

        return true;
    }

    @Override
    public boolean acceptFriend(AppUser currentUser, Long friendId) {
        Friendship friendship = friendshipServiceRepository.findFriendshipByTwoUsers(currentUser.getUserId(), friendId)
                .orElseThrow(() -> new CustomRuntimeException("Friend request not found", HttpStatus.NOT_FOUND));

        if(this.isMyFriend(currentUser, Optional.of(friendship))) {
            throw new CustomRuntimeException("You are already friends", HttpStatus.CONFLICT);
        }else if (this.isPendingFriendRequest(currentUser, Optional.of(friendship))) {
            friendshipServiceRepository.updateFriendshipStatusById(friendship.getId(), FriendshipStatus.ACCEPTED.toString());
            this.sendFriendRequestAcceptedNotification(currentUser, getUserById(friendId));
            return true;
        }
        throw new CustomRuntimeException("Friend request not found", HttpStatus.NOT_FOUND);
    }

    @Override
    public boolean cancelFriendRequest(AppUser currentUser, Long friendId) {
        Friendship friendship = friendshipServiceRepository.findFriendshipByTwoUsers(currentUser.getUserId(), friendId)
                .orElseThrow(() -> new CustomRuntimeException("Friend request not found", HttpStatus.NOT_FOUND));

        if(isPendingFriendRequest(currentUser, Optional.of(friendship))) {
            friendshipServiceRepository.delete(friendship);
            return true;
        }
        throw new CustomRuntimeException("Friend request not found", HttpStatus.NOT_FOUND);
    }

    @Override
    public boolean blockFriend(AppUser currentUser, Long friendId) {
        Friendship friendship = friendshipServiceRepository.findFriendshipByTwoUsers(currentUser.getUserId(), friendId)
                .orElseThrow(() -> new CustomRuntimeException("Friend request not found", HttpStatus.NOT_FOUND));

        if(isMyFriend(currentUser, Optional.of(friendship))) {
            friendshipServiceRepository.updateFriendshipStatusById(friendship.getId(), FriendshipStatus.BLOCKED.toString());
            return true;
        }
        throw new CustomRuntimeException("Friend request not found", HttpStatus.NOT_FOUND);
    }

    @Override
    public boolean unblockFriend(AppUser currentUser, Long friendId) {
        Friendship friendship = friendshipServiceRepository.findFriendshipByTwoUsers(currentUser.getUserId(), friendId)
                .orElseThrow(() -> new CustomRuntimeException("Friend request not found", HttpStatus.NOT_FOUND));

        if(isMyFriend(currentUser, Optional.of(friendship))) {
            friendshipServiceRepository.updateFriendshipStatusById(friendship.getId(), FriendshipStatus.ACCEPTED.toString());
            return true;
        }
        throw new CustomRuntimeException("Friend request not found", HttpStatus.NOT_FOUND);
    }




    /**
     * Get all friends of the user
     * @param currentUser
     * @return
     * @throws CustomRuntimeException
     * TODO: Implement add Pagination , pass Pagination as DTO
     */
    @Override
    public Set<AppUserResponseDto> getFriends(AppUser currentUser) {
        return getFriendsByStatus(currentUser, FriendshipStatus.ACCEPTED);
    }

    @Override
    public Set<AppUserResponseDto> getFriendRequests(AppUser currentUser) {
        return getFriendsByStatus(currentUser, FriendshipStatus.PENDING);
    }

    private Set<AppUserResponseDto> getFriendsByStatus(AppUser currentUser, FriendshipStatus status) {
        return friendshipServiceRepository.findFriendsByUserIdAndStatus(
                        currentUser.getUserId(), status.toString()
                ).stream()
                .map(userMapper::mapToAppUserResponseDto)
                .collect(Collectors.toSet());
    }

    private boolean isMyFriend(AppUser currentUser, Optional<Friendship> optionalFriendship) {
        return optionalFriendship
                .filter(friendship -> friendship.getStatus() == FriendshipStatus.ACCEPTED)
                .filter(friendship ->
                        friendship.getUser1().getUserId().equals(currentUser.getUserId()) ||
                                friendship.getUser2().getUserId().equals(currentUser.getUserId()))
                .isPresent();
    }

    private AppUser getUserById(Long userId) {
        return appUserRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private boolean isPendingFriendRequest(AppUser currentUser, Optional<Friendship> optionalFriendship) {
        return optionalFriendship
                .filter(friendship -> friendship.getStatus() == FriendshipStatus.PENDING)
                .filter(friendship ->
                        friendship.getUser1().getUserId().equals(currentUser.getUserId()) ||
                                friendship.getUser2().getUserId().equals(currentUser.getUserId()))
                .isPresent();
    }

    private void sendFriendRequestNotification(AppUser currentUser, AppUser friend) {
        NotificationEvent notificationEvent =  NotificationEvent.builder()
                .senderId(currentUser.getUserId())
                .receiverId(friend.getUserId())
                .type(NotificationType.REQUEST_FRIENDSHIP)
                .message(currentUser.getUserId() + " sent you a friend request")
                .build();

        notificationService.sendNotification(notificationEvent);
    }

    private void sendFriendRequestAcceptedNotification(AppUser currentUser, AppUser friend) {
        NotificationEvent notificationEvent =  NotificationEvent.builder()
                .senderId(currentUser.getUserId())
                .receiverId(friend.getUserId())
                .type(NotificationType.ACCEPT_FRIENDSHIP)
                .message(currentUser.getUserId() + " accepted your friend request")
                .build();

        notificationService.sendNotification(notificationEvent);
    }


}
