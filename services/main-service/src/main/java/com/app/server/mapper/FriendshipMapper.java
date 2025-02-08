package com.app.server.mapper;


import com.app.server.enums.FriendshipStatus;
import com.app.server.model.AppUser;
import com.app.server.model.Friendship;
import org.springframework.stereotype.Service;

@Service
public class FriendshipMapper {

    public Friendship mapToFriendship(AppUser currentUser, AppUser friend) {
        return Friendship.builder()
                .user1(currentUser)
                .user2(friend)
                .status(FriendshipStatus.PENDING)
                .build();
    }


}
