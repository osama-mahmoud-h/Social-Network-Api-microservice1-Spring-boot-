package com.app.server.mapper;


import com.app.server.enums.FriendshipStatus;
import com.app.server.model.Friendship;
import com.app.server.model.UserProfile;
import org.springframework.stereotype.Service;

@Service
public class FriendshipMapper {

    public Friendship mapToFriendship(UserProfile currentUser, UserProfile friend) {
        return Friendship.builder()
                .user1(currentUser)
                .user2(friend)
                .status(FriendshipStatus.PENDING)
                .build();
    }


}
