-- ================================================================
-- Chat Service — Cassandra Keyspace and Table Schema
-- Applied once at startup via cassandra-init container:
--   cqlsh cassandra -f /schema.cql
-- ================================================================

CREATE KEYSPACE IF NOT EXISTS chat_service
    WITH replication = {
        'class': 'NetworkTopologyStrategy',
        'datacenter1': 1
    }
    AND durable_writes = true;

USE chat_service;

-- ----------------------------------------------------------------
-- messages_by_conversation
-- Primary query: fetch N messages for a conversation, newest first.
-- Cursor-based pagination: WHERE message_id < :cursor LIMIT :n
--
-- Partition key  : conversation_id  — all messages for a conversation
--                  land on the same Cassandra partition (co-located I/O).
--                  For 1:1 chats: sorted(senderId, receiverId) joined by "_"
--                  For groups   : groupId
--
-- Clustering key : message_id DESC  — Snowflake ID in descending order.
--                  Snowflake IDs embed a timestamp in the high bits, so
--                  DESC order == newest-first without a separate sort pass.
--
-- LeveledCompactionStrategy: optimal for point reads by primary key
--   (chat history lookup). Keeps SSTable count low → fewer Bloom filter
--   checks per read. Chat history is read-heavy (10:1 read:write).
--
-- bloom_filter_fp_chance = 0.001: reduce false positives 10× vs default
--   0.01 — for non-existent conversation IDs Cassandra skips all disk I/O.
--
-- gc_grace_seconds = 86400 (1 day): messages are immutable after insert,
--   no deletes expected; 1-day grace covers node repair windows.
--
-- LZ4 compression: fast decompression for read-heavy workload, ~50% space.
-- ----------------------------------------------------------------
CREATE TABLE IF NOT EXISTS messages_by_conversation (
    conversation_id  TEXT,
    message_id       BIGINT,
    sender_id        TEXT,
    receiver_id      TEXT,
    content          TEXT,
    message_type     TEXT,
    conversation_type TEXT,
    created_at       TIMESTAMP,
    PRIMARY KEY ((conversation_id), message_id)
) WITH CLUSTERING ORDER BY (message_id DESC)
  AND bloom_filter_fp_chance = 0.001
  AND compaction = {
      'class': 'org.apache.cassandra.db.compaction.LeveledCompactionStrategy',
      'sstable_size_in_mb': '160'
  }
  AND compression = {
      'class': 'org.apache.cassandra.io.compress.LZ4Compressor'
  }
  AND gc_grace_seconds = 86400
  AND comment = 'Immutable chat messages. Tuned for read-heavy cursor-paginated history lookups.';

-- ----------------------------------------------------------------
-- user_conversations  (inbox view)
-- Primary query: list all conversations for a user, most recent first.
-- Cursor-based pagination: WHERE last_message_id < :cursor LIMIT :n
-- ----------------------------------------------------------------
CREATE TABLE IF NOT EXISTS user_conversations (
    user_id              BIGINT,
    last_message_id      BIGINT,
    conversation_id      TEXT,
    conversation_type    TEXT,
    conversation_name    TEXT,
    last_message_preview TEXT,
    unread_count         INT,
    PRIMARY KEY ((user_id), last_message_id)
) WITH CLUSTERING ORDER BY (last_message_id DESC)
  AND compaction = {
      'class': 'org.apache.cassandra.db.compaction.LeveledCompactionStrategy'
  }
  AND compression = {
      'class': 'org.apache.cassandra.io.compress.LZ4Compressor'
  }
  AND gc_grace_seconds = 86400
  AND comment = 'Inbox view per user. Updated on every new message in the conversation.';

-- ----------------------------------------------------------------
-- group_members
-- Primary query: fetch all members of a group.
-- ----------------------------------------------------------------
CREATE TABLE IF NOT EXISTS group_members (
    group_id    TEXT,
    user_id     BIGINT,
    role        TEXT,
    joined_at   TIMESTAMP,
    PRIMARY KEY ((group_id), user_id)
) WITH compaction = {
      'class': 'org.apache.cassandra.db.compaction.LeveledCompactionStrategy'
  }
  AND compression = {
      'class': 'org.apache.cassandra.io.compress.LZ4Compressor'
  }
  AND gc_grace_seconds = 86400
  AND comment = 'Group membership. Max 100 members per group.';

-- ----------------------------------------------------------------
-- user_groups
-- Primary query: fetch all groups a user belongs to.
-- ----------------------------------------------------------------
CREATE TABLE IF NOT EXISTS user_groups (
    user_id     BIGINT,
    group_id    TEXT,
    group_name  TEXT,
    joined_at   TIMESTAMP,
    PRIMARY KEY ((user_id), group_id)
) WITH compaction = {
      'class': 'org.apache.cassandra.db.compaction.LeveledCompactionStrategy'
  }
  AND compression = {
      'class': 'org.apache.cassandra.io.compress.LZ4Compressor'
  }
  AND gc_grace_seconds = 86400
  AND comment = 'Reverse index: groups per user.';