package semsem.chatservice.repository;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;
import semsem.chatservice.model.ConversationMessage;

import java.util.List;

@Repository
public interface ConversationMessageRepository extends CassandraRepository<ConversationMessage, String> {

    // Cursor-based pagination: messages older than the given Snowflake ID
    @Query("SELECT * FROM messages_by_conversation WHERE conversation_id = ?0 AND message_id < ?1 LIMIT ?2")
    List<ConversationMessage> findByConversationIdBeforeMessageId(String conversationId, long beforeMessageId, int limit);

    // Latest N messages (first page, no cursor)
    @Query("SELECT * FROM messages_by_conversation WHERE conversation_id = ?0 LIMIT ?1")
    List<ConversationMessage> findLatestByConversationId(String conversationId, int limit);
}