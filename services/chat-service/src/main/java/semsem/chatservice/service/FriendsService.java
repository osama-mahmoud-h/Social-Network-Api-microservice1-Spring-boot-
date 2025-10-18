package semsem.chatservice.service;

import org.springframework.data.domain.Page;
import semsem.chatservice.dto.response.AppUserForChatDto;

public interface FriendsService {
    Page<AppUserForChatDto> getFriendsPaginated(String token, int page, int size);
    boolean areFriends(String token, Long userId1, Long userId2);
}