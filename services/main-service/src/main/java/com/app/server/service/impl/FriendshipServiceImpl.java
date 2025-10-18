package com.app.server.service.impl;

import com.app.server.dto.notification.NotificationEvent;
import com.app.server.dto.response.AppUserResponseDto;
import com.app.server.enums.FriendshipStatus;
import com.app.server.enums.NotificationType;
import com.app.server.exception.CustomRuntimeException;
import com.app.server.mapper.FriendshipMapper;
import com.app.server.mapper.UserMapper;
import com.app.server.model.Friendship;
import com.app.server.model.UserProfile;
import com.app.server.repository.FriendshipServiceRepository;
import com.app.server.repository.UserProfileRepository;
import com.app.server.service.FriendshipService;
import com.app.server.service.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FriendshipServiceImpl implements FriendshipService {

    private final FriendshipServiceRepository friendshipServiceRepository;
    private final UserProfileRepository userProfileRepository;
    private final FriendshipMapper friendshipMapper;
    private final UserMapper userMapper;
    private final NotificationService notificationService;

    @Override
    public boolean addFriend(UserProfile currentUser, Long friendId) {
        UserProfile friend = getUserById(friendId);
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
    public boolean removeFriend(UserProfile currentUser, Long friendId) {
        Friendship optionalFriendship = friendshipServiceRepository.findFriendshipByTwoUsers(currentUser.getUserId(), friendId)
                .orElseThrow(() -> new CustomRuntimeException("You are not friends", HttpStatus.NOT_FOUND));

        if(!isMyFriend(currentUser, Optional.of(optionalFriendship))) {
            throw new CustomRuntimeException("You are not friends", HttpStatus.NOT_FOUND);
        }
        friendshipServiceRepository.delete(optionalFriendship);

        return true;
    }

    @Override
    public boolean acceptFriend(UserProfile currentUser, Long friendId) {
        Friendship friendship = friendshipServiceRepository.findFriendshipByTwoUsers(currentUser.getUserId(), friendId)
                .orElseThrow(() -> new CustomRuntimeException("Friend request not found", HttpStatus.NOT_FOUND));

        if(this.isMyFriend(currentUser, Optional.of(friendship))) {
            throw new CustomRuntimeException("You are already friends", HttpStatus.CONFLICT);
        }else if (this.isPendingFriendRequest(currentUser, Optional.of(friendship))) {
            friendshipServiceRepository.updateFriendshipStatusById(friendship.getFriendshipId(), FriendshipStatus.ACCEPTED.toString());
            this.sendFriendRequestAcceptedNotification(currentUser, getUserById(friendId));
            return true;
        }
        throw new CustomRuntimeException("Friend request not found", HttpStatus.NOT_FOUND);
    }

    @Override
    public boolean cancelFriendRequest(UserProfile currentUser, Long friendId) {
        Friendship friendship = friendshipServiceRepository.findFriendshipByTwoUsers(currentUser.getUserId(), friendId)
                .orElseThrow(() -> new CustomRuntimeException("Friend request not found", HttpStatus.NOT_FOUND));

        if(isPendingFriendRequest(currentUser, Optional.of(friendship))) {
            friendshipServiceRepository.delete(friendship);
            return true;
        }
        throw new CustomRuntimeException("Friend request not found", HttpStatus.NOT_FOUND);
    }

    @Override
    public boolean blockFriend(UserProfile currentUser, Long friendId) {
        Friendship friendship = friendshipServiceRepository.findFriendshipByTwoUsers(currentUser.getUserId(), friendId)
                .orElseThrow(() -> new CustomRuntimeException("Friend request not found", HttpStatus.NOT_FOUND));

        if(isMyFriend(currentUser, Optional.of(friendship))) {
            friendshipServiceRepository.updateFriendshipStatusById(friendship.getFriendshipId(), FriendshipStatus.BLOCKED.toString());
            return true;
        }
        throw new CustomRuntimeException("Friend request not found", HttpStatus.NOT_FOUND);
    }

    @Override
    public boolean unblockFriend(UserProfile currentUser, Long friendId) {
        Friendship friendship = friendshipServiceRepository.findFriendshipByTwoUsers(currentUser.getUserId(), friendId)
                .orElseThrow(() -> new CustomRuntimeException("Friend request not found", HttpStatus.NOT_FOUND));

        if(isMyFriend(currentUser, Optional.of(friendship))) {
            friendshipServiceRepository.updateFriendshipStatusById(friendship.getFriendshipId(), FriendshipStatus.ACCEPTED.toString());
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
    public Set<AppUserResponseDto> getFriends(UserProfile currentUser) {
        return getFriendsByStatus(currentUser, FriendshipStatus.ACCEPTED);
    }

    @Override
    public Page<AppUserResponseDto> getFriendsPaginated(Long currentUser, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<UserProfile> friends = friendshipServiceRepository.findFriendsPaginated(currentUser, pageable);

        List<AppUserResponseDto> friendDtos = friends.stream()
                .map(userMapper::mapToAppUserResponseDto)
                .collect(Collectors.toList());

        // Get total count for pagination
        long totalCount = friendshipServiceRepository.findFriendsByUserIdAndStatus(
                currentUser, FriendshipStatus.ACCEPTED.toString()
        ).size();

        return new PageImpl<>(friendDtos, pageable, totalCount);
    }

    @Override
    public Set<AppUserResponseDto> getFriendRequests(UserProfile currentUser) {
        return getFriendsByStatus(currentUser, FriendshipStatus.PENDING);
    }

    @Override
    public int getMutualFriendsCount(UserProfile currentUserDetails, Long friendId) {
        return friendshipServiceRepository.getCountOfMutualFriends(currentUserDetails.getUserId(), friendId);
    }

    @Override
    public Set<AppUserResponseDto> getMutualFriends(UserProfile currentUserDetails, Long friendId) {
        return friendshipServiceRepository.findMutualFriends(currentUserDetails.getUserId(), friendId)
                .stream()
                .map(userMapper::mapToAppUserResponseDto)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<AppUserResponseDto> suggestFriends(Long currentUserDetails) {
        return friendshipServiceRepository.findFriendSuggestions(currentUserDetails).stream()
                        .map(this::mapRawUserToResponseDto).collect(Collectors.toSet());

    }

    private AppUserResponseDto mapRawUserToResponseDto(Object[] row) {
        return new AppUserResponseDto(
                (Long) row[0], // userId
                (String) row[3] + " " + (String) row[4], // username
                (String) row[2], // email
                null  // image_url
        );
    }

    private Set<AppUserResponseDto> getFriendsByStatus(UserProfile currentUser, FriendshipStatus status) {
        return friendshipServiceRepository.findFriendsByUserIdAndStatus(
                        currentUser.getUserId(), status.toString()
                ).stream()
                .map(this::mapRawUserToResponseDto)
                .collect(Collectors.toSet());
    }

    private boolean isMyFriend(UserProfile currentUser, Optional<Friendship> optionalFriendship) {
        return optionalFriendship
                .filter(friendship -> friendship.getStatus() == FriendshipStatus.ACCEPTED)
                .filter(friendship ->
                        friendship.getUser1().getUserId().equals(currentUser.getUserId()) ||
                                friendship.getUser2().getUserId().equals(currentUser.getUserId()))
                .isPresent();
    }

    private UserProfile getUserById(Long userId) {
        return userProfileRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private boolean isPendingFriendRequest(UserProfile currentUser, Optional<Friendship> optionalFriendship) {
        return optionalFriendship
                .filter(friendship -> friendship.getStatus() == FriendshipStatus.PENDING)
                .filter(friendship ->
                        friendship.getUser1().getUserId().equals(currentUser.getUserId()) ||
                                friendship.getUser2().getUserId().equals(currentUser.getUserId()))
                .isPresent();
    }

    private void sendFriendRequestNotification(UserProfile currentUser, UserProfile friend) {
        NotificationEvent notificationEvent =  NotificationEvent.builder()
                .senderId(currentUser.getUserId())
                .receiverId(friend.getUserId())
                .type(NotificationType.REQUEST_FRIENDSHIP)
                .message(currentUser.getUserId() + " sent you a friend request")
                .build();

        notificationService.sendNotification(notificationEvent);
    }

    private void sendFriendRequestAcceptedNotification(UserProfile currentUser, UserProfile friend) {
        NotificationEvent notificationEvent =  NotificationEvent.builder()
                .senderId(currentUser.getUserId())
                .receiverId(friend.getUserId())
                .type(NotificationType.ACCEPT_FRIENDSHIP)
                .message(currentUser.getUserId() + " accepted your friend request")
                .build();

        notificationService.sendNotification(notificationEvent);
    }


}
