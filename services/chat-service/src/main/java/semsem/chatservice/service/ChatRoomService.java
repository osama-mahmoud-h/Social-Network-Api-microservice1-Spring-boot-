package semsem.chatservice.service;

import java.util.Optional;

public interface ChatRoomService {

    String createChatId(String senderId, String receiverId);
}
