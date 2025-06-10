package semsem.chatservice.service;

import java.util.Optional;

public interface ChatRoomService {
    Optional<String> getChatRoomId(String senderId, String receiverId);
}
