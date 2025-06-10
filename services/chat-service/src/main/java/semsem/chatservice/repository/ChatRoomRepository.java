package semsem.chatservice.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import semsem.chatservice.model.ChatRoom;

import java.util.Optional;

@Repository
public interface ChatRoomRepository extends MongoRepository<ChatRoom, String> {

    //Optional<ChatRoom> findBySenderIdAndReceiverId(String senderId, String receiverId);

    Optional<ChatRoom> findChatRoomByChatId(String chatId);
}
