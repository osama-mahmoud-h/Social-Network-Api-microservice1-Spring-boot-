package semsem.chatservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.CassandraType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.Instant;

@Table("messages_by_conversation")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConversationMessage {

    @PrimaryKeyColumn(name = "conversation_id", type = PrimaryKeyType.PARTITIONED)
    private String conversationId;

    // Clustering DESC: newest first — Snowflake ID doubles as cursor for pagination
    @PrimaryKeyColumn(name = "message_id", type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
    private long messageId;

    @Column("sender_id")
    private String senderId;

    @Column("receiver_id")
    private String receiverId;

    @Column("content")
    private String content;

    @Column("message_type")
    @CassandraType(type = CassandraType.Name.TEXT)
    private String messageType;

    @Column("conversation_type")
    @CassandraType(type = CassandraType.Name.TEXT)
    private String conversationType;

    @Column("created_at")
    private Instant createdAt;
}