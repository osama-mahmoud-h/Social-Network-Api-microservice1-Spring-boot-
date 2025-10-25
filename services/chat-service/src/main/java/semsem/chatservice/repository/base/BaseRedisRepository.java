package semsem.chatservice.repository.base;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;

/**
 * Base Redis Repository providing core CRUD operations for all Redis repositories.
 * This class encapsulates common Redis operations to promote code reuse and separation of concerns.
 *
 * @param <V> The value type stored in Redis
 */
@Slf4j
public abstract class BaseRedisRepository<V> {

    protected final RedisTemplate<String, Object> redisTemplate;
    protected final String keyPrefix;

    protected BaseRedisRepository(RedisTemplate<String, Object> redisTemplate, String keyPrefix) {
        this.redisTemplate = redisTemplate;
        this.keyPrefix = keyPrefix;
    }

    /**
     * Generate a Redis key with the configured prefix
     *
     * @param identifier The unique identifier for the key
     * @return Full Redis key with prefix
     */
    protected String generateKey(String identifier) {
        return keyPrefix + identifier;
    }

    /**
     * Check if a key exists in Redis
     *
     * @param key The Redis key to check
     * @return true if key exists, false otherwise
     */
    public boolean exists(String key) {
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(generateKey(key)));
        } catch (Exception e) {
            log.error("Error checking key existence in Redis: key={}, error={}", key, e.getMessage(), e);
            return false;
        }
    }

    /**
     * Delete a key from Redis
     *
     * @param key The Redis key to delete
     * @return true if deletion was successful, false otherwise
     */
    public boolean delete(String key) {
        try {
            Boolean deleted = redisTemplate.delete(generateKey(key));
            log.debug("Key deleted from Redis: key={}, success={}", key, deleted);
            return Boolean.TRUE.equals(deleted);
        } catch (Exception e) {
            log.error("Error deleting key from Redis: key={}, error={}", key, e.getMessage(), e);
            return false;
        }
    }

    /**
     * Set expiration time for a key
     *
     * @param key The Redis key
     * @param timeout The timeout value
     * @param unit The time unit
     * @return true if expiration was set successfully, false otherwise
     */
    public boolean expire(String key, long timeout, TimeUnit unit) {
        try {
            Boolean result = redisTemplate.expire(generateKey(key), timeout, unit);
            log.debug("Expiration set for key: key={}, timeout={}, unit={}, success={}",
                     key, timeout, unit, result);
            return Boolean.TRUE.equals(result);
        } catch (Exception e) {
            log.error("Error setting expiration for key: key={}, error={}", key, e.getMessage(), e);
            return false;
        }
    }

    /**
     * Get the time to live for a key
     *
     * @param key The Redis key
     * @param unit The time unit
     * @return The time to live, or -1 if key doesn't exist or has no expiration
     */
    public long getTimeToLive(String key, TimeUnit unit) {
        try {
            Long ttl = redisTemplate.getExpire(generateKey(key), unit);
            return ttl != null ? ttl : -1L;
        } catch (Exception e) {
            log.error("Error getting TTL for key: key={}, error={}", key, e.getMessage(), e);
            return -1L;
        }
    }

    /**
     * Refresh the TTL of a key by resetting it to a new value
     *
     * @param key The Redis key
     * @param timeout The new timeout value
     * @param unit The time unit
     * @return true if TTL was refreshed successfully, false otherwise
     */
    public boolean refreshTTL(String key, long timeout, TimeUnit unit) {
        try {
            if (!exists(key)) {
                log.warn("Cannot refresh TTL for non-existent key: {}", key);
                return false;
            }
            return expire(key, timeout, unit);
        } catch (Exception e) {
            log.error("Error refreshing TTL for key: key={}, error={}", key, e.getMessage(), e);
            return false;
        }
    }

    /**
     * Hook method for subclasses to implement custom logging
     *
     * @param operation The operation being performed
     * @param key The key involved
     * @param success Whether the operation was successful
     */
    protected void logOperation(String operation, String key, boolean success) {
        if (success) {
            log.debug("{} completed successfully for key: {}", operation, key);
        } else {
            log.warn("{} failed for key: {}", operation, key);
        }
    }
}
