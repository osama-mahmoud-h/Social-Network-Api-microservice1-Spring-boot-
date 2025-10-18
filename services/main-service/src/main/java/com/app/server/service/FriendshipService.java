package com.app.server.service;

import com.app.server.dto.response.AppUserResponseDto;
import com.app.server.model.UserProfile;
import org.springframework.data.domain.Page;

import java.util.Set;

public interface FriendshipService {

    boolean addFriend(UserProfile currentUser, Long friendId);

    boolean removeFriend(UserProfile currentUser, Long friendId);

    boolean acceptFriend(UserProfile currentUser, Long friendId);

    boolean cancelFriendRequest(UserProfile currentUser, Long friendId);

    boolean blockFriend(UserProfile currentUser, Long friendId);

    boolean unblockFriend(UserProfile currentUser, Long friendId);

    Set<AppUserResponseDto> getFriends(UserProfile currentUser);

    Page<AppUserResponseDto> getFriendsPaginated(Long currentUser, int page, int size);

    Set<AppUserResponseDto> getFriendRequests(UserProfile currentUser);

    int getMutualFriendsCount(UserProfile currentUserDetails, Long friendId);

    Set<AppUserResponseDto> getMutualFriends(UserProfile currentUserDetails, Long friendId);

    Set<AppUserResponseDto> suggestFriends(Long currentUserDetails);
}
