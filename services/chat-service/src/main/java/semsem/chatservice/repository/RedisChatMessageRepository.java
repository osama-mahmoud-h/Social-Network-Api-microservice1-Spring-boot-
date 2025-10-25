package semsem.chatservice.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import semsem.chatservice.model.ChatMessage;
import semsem.chatservice.repository.base.BaseRedisHashRepository;
import semsem.chatservice.repository.base.BaseRedisListRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Repository
@Slf4j
public class RedisChatMessageRepository {

    private static final String CHAT_KEY_PREFIX = "chat:messages:";
    private static final String PRIVATE_CHAT_KEY_PREFIX = "chat:private:";
    private static final long MESSAGE_TTL_HOURS = 24; // Messages expire after 24 hours

    private final ChatMessageListRepository messageListRepository;
    private final PrivateChatHashRepository privateChatHashRepository;

    public RedisChatMessageRepository(RedisTemplate<String, Object> redisTemplate) {
        this.messageListRepository = new ChatMessageListRepository(redisTemplate, CHAT_KEY_PREFIX);
        this.privateChatHashRepository = new PrivateChatHashRepository(redisTemplate, PRIVATE_CHAT_KEY_PREFIX);
    }

    // ============================================================================
    // Business Logic Methods - Chat Message Operations
    // ============================================================================

    /**
     * Save a chat message to Redis with TTL
     */
    public void saveMessage(ChatMessage message) {
        try {
            String chatId = message.getChatId();
            messageListRepository.rightPush(chatId, message);
            messageListRepository.expire(chatId, MESSAGE_TTL_HOURS, TimeUnit.HOURS);
            log.debug("Message saved to Redis: chatId={}, messageId={}", chatId, message.getMessageId());
        } catch (Exception e) {
            log.error("Error saving message to Redis: {}", e.getMessage(), e);
        }
    }

    /**
     * Get all messages for a chat room from Redis
     */
    public List<ChatMessage> getMessagesByChatId(String chatId) {
        try {
            List<Object> messages = messageListRepository.getAll(chatId);

            if (messages.isEmpty()) {
                log.debug("No messages found in Redis for chatId={}", chatId);
                return new ArrayList<>();
            }

            List<ChatMessage> chatMessages = new ArrayList<>();
            for (Object obj : messages) {
                if (obj instanceof ChatMessage) {
                    chatMessages.add((ChatMessage) obj);
                }
            }

            log.debug("Retrieved {} messages from Redis for chatId={}", chatMessages.size(), chatId);
            return chatMessages;
        } catch (Exception e) {
            log.error("Error retrieving messages from Redis: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    /**
     * Get recent messages (last N messages) from Redis
     */
    public List<ChatMessage> getRecentMessages(String chatId, int count) {
        try {
            long totalSize = messageListRepository.size(chatId);
            long start = Math.max(0, totalSize - count);
            List<Object> messages = messageListRepository.range(chatId, start, -1);

            if (messages.isEmpty()) {
                return new ArrayList<>();
            }

            List<ChatMessage> chatMessages = new ArrayList<>();
            for (Object obj : messages) {
                if (obj instanceof ChatMessage) {
                    chatMessages.add((ChatMessage) obj);
                }
            }

            return chatMessages;
        } catch (Exception e) {
            log.error("Error retrieving recent messages from Redis: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    /**
     * Delete all messages for a specific chat
     */
    public void deleteChatMessages(String chatId) {
        try {
            messageListRepository.delete(chatId);
            log.debug("Deleted messages from Redis for chatId={}", chatId);
        } catch (Exception e) {
            log.error("Error deleting messages from Redis: {}", e.getMessage(), e);
        }
    }

    /**
     * Check if chat exists in Redis
     */
    public boolean chatExists(String chatId) {
        try {
            return messageListRepository.exists(chatId);
        } catch (Exception e) {
            log.error("Error checking chat existence in Redis: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Get message count for a chat
     */
    public long getMessageCount(String chatId) {
        try {
            return messageListRepository.size(chatId);
        } catch (Exception e) {
            log.error("Error getting message count from Redis: {}", e.getMessage(), e);
            return 0;
        }
    }

    // ============================================================================
    // Business Logic Methods - Private Chat Operations
    // ============================================================================

    /**
     * Cache private chat info (users in private chat)
     */
    public void cachePrivateChatUsers(String chatId, String senderId, String receiverId) {
        try {
            privateChatHashRepository.put(chatId, "senderId", senderId);
            privateChatHashRepository.put(chatId, "receiverId", receiverId);
            privateChatHashRepository.expire(chatId, MESSAGE_TTL_HOURS, TimeUnit.HOURS);
            log.debug("Cached private chat users for chatId={}", chatId);
        } catch (Exception e) {
            log.error("Error caching private chat users: {}", e.getMessage(), e);
        }
    }

    /**
     * Get private chat users from cache
     */
    public String[] getPrivateChatUsers(String chatId) {
        try {
            Object senderId = privateChatHashRepository.get(chatId, "senderId");
            Object receiverId = privateChatHashRepository.get(chatId, "receiverId");

            if (senderId != null && receiverId != null) {
                return new String[]{senderId.toString(), receiverId.toString()};
            }
        } catch (Exception e) {
            log.error("Error getting private chat users from Redis: {}", e.getMessage(), e);
        }
        return null;
    }


    /**
     * Repository for chat message list operations
     * Extends BaseRedisListRepository to inherit core List CRUD operations
     */
    private static class ChatMessageListRepository extends BaseRedisListRepository<ChatMessage> {
        public ChatMessageListRepository(RedisTemplate<String, Object> redisTemplate, String keyPrefix) {
            super(redisTemplate, keyPrefix);
        }
    }

    /**
     * Repository for private chat hash operations
     * Extends BaseRedisHashRepository to inherit core Hash CRUD operations
     */
    private static class PrivateChatHashRepository extends BaseRedisHashRepository<String, String> {
        public PrivateChatHashRepository(RedisTemplate<String, Object> redisTemplate, String keyPrefix) {
            super(redisTemplate, keyPrefix);
        }
    }
}
