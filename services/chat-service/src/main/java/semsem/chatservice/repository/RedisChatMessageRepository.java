package semsem.chatservice.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import semsem.chatservice.model.ChatMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
@Slf4j
public class RedisChatMessageRepository {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String CHAT_KEY_PREFIX = "chat:messages:";
    private static final String PRIVATE_CHAT_KEY_PREFIX = "chat:private:";
    private static final long MESSAGE_TTL_HOURS = 24; // Messages expire after 24 hours

    /**
     * Save a chat message to Redis with TTL
     */
    public void saveMessage(ChatMessage message) {
        try {
            String key = getChatKey(message.getChatId());
            redisTemplate.opsForList().rightPush(key, message);
            // Set expiration for the key (refresh TTL on each new message)
            redisTemplate.expire(key, MESSAGE_TTL_HOURS, TimeUnit.HOURS);
            log.debug("Message saved to Redis: chatId={}, messageId={}", message.getChatId(), message.getMessageId());
        } catch (Exception e) {
            log.error("Error saving message to Redis: {}", e.getMessage(), e);
        }
    }

    /**
     * Get all messages for a chat room from Redis
     */
    public List<ChatMessage> getMessagesByChatId(String chatId) {
        try {
            String key = getChatKey(chatId);
            List<Object> messages = redisTemplate.opsForList().range(key, 0, -1);

            if (messages == null || messages.isEmpty()) {
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
            String key = getChatKey(chatId);
            long start = Math.max(0, redisTemplate.opsForList().size(key) - count);
            List<Object> messages = redisTemplate.opsForList().range(key, start, -1);

            if (messages == null || messages.isEmpty()) {
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
            String key = getChatKey(chatId);
            redisTemplate.delete(key);
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
            String key = getChatKey(chatId);
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
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
            String key = getChatKey(chatId);
            Long size = redisTemplate.opsForList().size(key);
            return size != null ? size : 0;
        } catch (Exception e) {
            log.error("Error getting message count from Redis: {}", e.getMessage(), e);
            return 0;
        }
    }

    /**
     * Cache private chat info (users in private chat)
     */
    public void cachePrivateChatUsers(String chatId, String senderId, String receiverId) {
        try {
            String key = PRIVATE_CHAT_KEY_PREFIX + chatId;
            redisTemplate.opsForHash().put(key, "senderId", senderId);
            redisTemplate.opsForHash().put(key, "receiverId", receiverId);
            redisTemplate.expire(key, MESSAGE_TTL_HOURS, TimeUnit.HOURS);
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
            String key = PRIVATE_CHAT_KEY_PREFIX + chatId;
            Object senderId = redisTemplate.opsForHash().get(key, "senderId");
            Object receiverId = redisTemplate.opsForHash().get(key, "receiverId");

            if (senderId != null && receiverId != null) {
                return new String[]{senderId.toString(), receiverId.toString()};
            }
        } catch (Exception e) {
            log.error("Error getting private chat users from Redis: {}", e.getMessage(), e);
        }
        return null;
    }

    private String getChatKey(String chatId) {
        return CHAT_KEY_PREFIX + chatId;
    }
}