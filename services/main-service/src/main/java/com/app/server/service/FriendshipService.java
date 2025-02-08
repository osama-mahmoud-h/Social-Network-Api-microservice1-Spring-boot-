package com.app.server.service;

import com.app.server.dto.response.AppUserResponseDto;
import com.app.server.model.AppUser;

import java.util.Set;

public interface FriendshipService {

    boolean addFriend(AppUser currentUser, Long friendId);

    boolean removeFriend(AppUser currentUser, Long friendId);

    boolean acceptFriend(AppUser currentUser, Long friendId);

    boolean cancelFriendRequest(AppUser currentUser, Long friendId);

    boolean blockFriend(AppUser currentUser, Long friendId);

    boolean unblockFriend(AppUser currentUser, Long friendId);

    Set<AppUserResponseDto> getFriends(AppUser currentUser);

    Set<AppUserResponseDto> getFriendRequests(AppUser currentUser);
}
