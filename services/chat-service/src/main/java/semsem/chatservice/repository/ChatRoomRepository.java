package semsem.chatservice.repository;

import org.springframework.stereotype.Repository;
import semsem.chatservice.model.ChatRoom;

import java.util.Optional;

@Repository
public interface ChatRoomRepository  {

    //Optional<ChatRoom> findBySenderIdAndReceiverId(String senderId, String receiverId);

    Optional<ChatRoom> findChatRoomByChatId(String chatId);
}
