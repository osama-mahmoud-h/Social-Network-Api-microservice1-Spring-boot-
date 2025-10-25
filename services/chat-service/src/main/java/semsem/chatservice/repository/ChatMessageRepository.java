package semsem.chatservice.repository;

//import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import semsem.chatservice.model.ChatMessage;

import java.util.List;

@Repository
public interface ChatMessageRepository {

    List<ChatMessage> findChatMessagesByChatId(String chatRoomId);
}
