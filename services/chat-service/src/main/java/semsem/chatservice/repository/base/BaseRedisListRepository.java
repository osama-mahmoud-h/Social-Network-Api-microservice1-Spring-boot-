package semsem.chatservice.repository.base;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * Base Redis Repository for List operations.
 * Provides core CRUD operations for Redis List data structure.
 *
 * @param <T> The type of elements stored in the list
 */
@Slf4j
public abstract class BaseRedisListRepository<T> extends BaseRedisRepository<T> {

    protected BaseRedisListRepository(RedisTemplate<String, Object> redisTemplate, String keyPrefix) {
        super(redisTemplate, keyPrefix);
    }

    /**
     * Push an element to the right (end) of the list
     *
     * @param key The Redis key
     * @param value The value to push
     * @return The size of the list after the push operation
     */
    public Long rightPush(String key, T value) {
        try {
            Long size = redisTemplate.opsForList().rightPush(generateKey(key), value);
            log.debug("Right pushed value to list: key={}, size={}", key, size);
            return size;
        } catch (Exception e) {
            log.error("Error pushing value to list: key={}, error={}", key, e.getMessage(), e);
            return null;
        }
    }

    /**
     * Push an element to the left (beginning) of the list
     *
     * @param key The Redis key
     * @param value The value to push
     * @return The size of the list after the push operation
     */
    public Long leftPush(String key, T value) {
        try {
            Long size = redisTemplate.opsForList().leftPush(generateKey(key), value);
            log.debug("Left pushed value to list: key={}, size={}", key, size);
            return size;
        } catch (Exception e) {
            log.error("Error pushing value to list: key={}, error={}", key, e.getMessage(), e);
            return null;
        }
    }

    /**
     * Pop an element from the right (end) of the list
     *
     * @param key The Redis key
     * @return The popped value, or null if list is empty
     */
    public Object rightPop(String key) {
        try {
            Object value = redisTemplate.opsForList().rightPop(generateKey(key));
            log.debug("Right popped value from list: key={}", key);
            return value;
        } catch (Exception e) {
            log.error("Error popping value from list: key={}, error={}", key, e.getMessage(), e);
            return null;
        }
    }

    /**
     * Pop an element from the left (beginning) of the list
     *
     * @param key The Redis key
     * @return The popped value, or null if list is empty
     */
    public Object leftPop(String key) {
        try {
            Object value = redisTemplate.opsForList().leftPop(generateKey(key));
            log.debug("Left popped value from list: key={}", key);
            return value;
        } catch (Exception e) {
            log.error("Error popping value from list: key={}, error={}", key, e.getMessage(), e);
            return null;
        }
    }

    /**
     * Get a range of elements from the list
     *
     * @param key The Redis key
     * @param start The start index (0-based)
     * @param end The end index (-1 for end of list)
     * @return List of elements in the specified range
     */
    public List<Object> range(String key, long start, long end) {
        try {
            List<Object> result = redisTemplate.opsForList().range(generateKey(key), start, end);
            log.debug("Retrieved range from list: key={}, start={}, end={}, count={}",
                     key, start, end, result != null ? result.size() : 0);
            return result != null ? result : new ArrayList<>();
        } catch (Exception e) {
            log.error("Error getting range from list: key={}, error={}", key, e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    /**
     * Get all elements from the list
     *
     * @param key The Redis key
     * @return All elements in the list
     */
    public List<Object> getAll(String key) {
        return range(key, 0, -1);
    }

    /**
     * Get the size of the list
     *
     * @param key The Redis key
     * @return The size of the list
     */
    public long size(String key) {
        try {
            Long size = redisTemplate.opsForList().size(generateKey(key));
            return size != null ? size : 0L;
        } catch (Exception e) {
            log.error("Error getting list size: key={}, error={}", key, e.getMessage(), e);
            return 0L;
        }
    }

    /**
     * Trim the list to the specified range
     *
     * @param key The Redis key
     * @param start The start index
     * @param end The end index
     */
    public void trim(String key, long start, long end) {
        try {
            redisTemplate.opsForList().trim(generateKey(key), start, end);
            log.debug("Trimmed list: key={}, start={}, end={}", key, start, end);
        } catch (Exception e) {
            log.error("Error trimming list: key={}, error={}", key, e.getMessage(), e);
        }
    }

    /**
     * Set a value at a specific index in the list
     *
     * @param key The Redis key
     * @param index The index
     * @param value The value to set
     */
    public void set(String key, long index, T value) {
        try {
            redisTemplate.opsForList().set(generateKey(key), index, value);
            log.debug("Set value in list: key={}, index={}", key, index);
        } catch (Exception e) {
            log.error("Error setting value in list: key={}, index={}, error={}", key, index, e.getMessage(), e);
        }
    }

    /**
     * Get a value at a specific index in the list
     *
     * @param key The Redis key
     * @param index The index
     * @return The value at the specified index
     */
    public Object get(String key, long index) {
        try {
            Object value = redisTemplate.opsForList().index(generateKey(key), index);
            log.debug("Retrieved value from list: key={}, index={}", key, index);
            return value;
        } catch (Exception e) {
            log.error("Error getting value from list: key={}, index={}, error={}", key, index, e.getMessage(), e);
            return null;
        }
    }

    /**
     * Remove elements from the list
     *
     * @param key The Redis key
     * @param count The number of occurrences to remove
     * @param value The value to remove
     * @return The number of removed elements
     */
    public Long remove(String key, long count, Object value) {
        try {
            Long removed = redisTemplate.opsForList().remove(generateKey(key), count, value);
            log.debug("Removed elements from list: key={}, count={}", key, removed);
            return removed;
        } catch (Exception e) {
            log.error("Error removing elements from list: key={}, error={}", key, e.getMessage(), e);
            return 0L;
        }
    }
}
