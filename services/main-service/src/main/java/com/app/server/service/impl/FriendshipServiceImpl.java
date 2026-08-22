package com.app.server.service.impl;

import com.app.server.dto.response.AppUserResponseDto;
import com.app.shared.events.type.FriendshipActionType;
import com.app.server.enums.FriendshipStatus;
import com.app.shared.events.type.NotificationType;
import com.app.server.event.app.domain.FeedFriendshipDomainEvent;
import com.app.server.event.app.domain.FriendshipDomainEvent;
import com.app.server.exception.CustomRuntimeException;
import com.app.server.exception.IllegalFriendshipTransition;
import com.app.server.mapper.UserMapper;
import com.app.server.model.Friendship;
import com.app.server.model.UserProfile;
import com.app.server.repository.FriendshipServiceRepository;
import com.app.server.repository.UserProfileRepository;
import com.app.server.service.FriendshipService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FriendshipServiceImpl implements FriendshipService {

    private final FriendshipServiceRepository friendshipServiceRepository;
    private final UserProfileRepository userProfileRepository;
    private final UserMapper userMapper;
    private final com.app.server.mapper.UserProjectionMapper userProjectionMapper;
    private final ApplicationEventPublisher eventPublisher;

    // Each mutation is: load the aggregate → call the transition → save. The rules about which
    // status and which actor allow the transition live in Friendship, not here.

    @Override
    @Transactional
    public boolean addFriend(UserProfile currentUser, Long friendId) {
        UserProfile friend = getUserById(friendId);
        friendshipServiceRepository.findFriendshipByTwoUsers(currentUser.getUserId(), friendId)
                .ifPresent(existing -> {
                    throw new IllegalFriendshipTransition(existing.isAccepted()
                            ? "You are already friends"
                            : "A friendship already exists between you and this user");
                });

        friendshipServiceRepository.save(Friendship.request(currentUser, friend));
        this.sendFriendRequestNotification(currentUser, friend);
        return true;
    }

    @Override
    @Transactional
    public boolean removeFriend(UserProfile currentUser, Long friendId) {
        Friendship friendship = loadFriendship(currentUser, friendId);

        friendship.remove(currentUser.getUserId());
        friendshipServiceRepository.delete(friendship);

        eventPublisher.publishEvent(new FeedFriendshipDomainEvent(
                currentUser.getUserId(), FriendshipActionType.REMOVED,
                currentUser.getUserId(), friendId));
        return true;
    }

    @Override
    @Transactional
    public boolean acceptFriend(UserProfile currentUser, Long friendId) {
        Friendship friendship = loadFriendship(currentUser, friendId);

        friendship.accept(currentUser.getUserId());
        friendshipServiceRepository.save(friendship);

        this.sendFriendRequestAcceptedNotification(currentUser, getUserById(friendId));
        eventPublisher.publishEvent(new FeedFriendshipDomainEvent(
                currentUser.getUserId(), FriendshipActionType.ACCEPTED,
                currentUser.getUserId(), friendId));
        return true;
    }

    @Override
    @Transactional
    public boolean cancelFriendRequest(UserProfile currentUser, Long friendId) {
        Friendship friendship = loadFriendship(currentUser, friendId);

        friendship.cancel(currentUser.getUserId());
        friendshipServiceRepository.delete(friendship);
        return true;
    }

    @Override
    @Transactional
    public boolean blockFriend(UserProfile currentUser, Long friendId) {
        Friendship friendship = loadFriendship(currentUser, friendId);

        friendship.block(currentUser.getUserId());
        friendshipServiceRepository.save(friendship);

        eventPublisher.publishEvent(new FeedFriendshipDomainEvent(
                currentUser.getUserId(), FriendshipActionType.BLOCKED,
                currentUser.getUserId(), friendId));
        return true;
    }

    @Override
    @Transactional
    public boolean unblockFriend(UserProfile currentUser, Long friendId) {
        Friendship friendship = loadFriendship(currentUser, friendId);

        friendship.unblock(currentUser.getUserId());
        friendshipServiceRepository.save(friendship);
        return true;
    }

    private Friendship loadFriendship(UserProfile currentUser, Long friendId) {
        return friendshipServiceRepository.findFriendshipByTwoUsers(currentUser.getUserId(), friendId)
                .orElseThrow(() -> new CustomRuntimeException("Friendship not found", HttpStatus.NOT_FOUND));
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
        // ✅ Type-safe projection mapping - IDE autocomplete works!
        return friendshipServiceRepository.findFriendSuggestions(currentUserDetails).stream()
                .map(userProjectionMapper::toAppUserResponseDto)
                .collect(Collectors.toSet());
    }

    private Set<AppUserResponseDto> getFriendsByStatus(UserProfile currentUser, FriendshipStatus status) {
        // ✅ Type-safe projection mapping - no more Object[] casting!
        return friendshipServiceRepository.findFriendsByUserIdAndStatus(
                        currentUser.getUserId(), status.toString()
                ).stream()
                .map(userProjectionMapper::toAppUserResponseDto)
                .collect(Collectors.toSet());
    }

    private UserProfile getUserById(Long userId) {
        return userProfileRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private void sendFriendRequestNotification(UserProfile currentUser, UserProfile friend) {
        log.debug("Publishing friendship domain event: REQUEST, senderId={}, receiverId={}",
                  currentUser.getUserId(), friend.getUserId());

        FriendshipDomainEvent event = new FriendshipDomainEvent(
            currentUser.getUserId(),
            NotificationType.REQUEST_FRIENDSHIP,
            currentUser.getUserId(),
            friend.getUserId(),
            currentUser.getUserId() + " sent you a friend request"
        );

        eventPublisher.publishEvent(event);
    }

    /**
     * Publishes domain event for friend request acceptance
     * Event will be asynchronously converted to Kafka message by DomainEventPublisher
     */
    private void sendFriendRequestAcceptedNotification(UserProfile currentUser, UserProfile friend) {
        log.debug("Publishing friendship domain event: ACCEPT, senderId={}, receiverId={}",
                  currentUser.getUserId(), friend.getUserId());

        FriendshipDomainEvent event = new FriendshipDomainEvent(
            currentUser.getUserId(),
            NotificationType.ACCEPT_FRIENDSHIP,
            currentUser.getUserId(),
            friend.getUserId(),
            currentUser.getUserId() + " accepted your friend request"
        );

        eventPublisher.publishEvent(event);
    }


}
