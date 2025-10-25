package semsem.chatservice.repository.base;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * Base Redis Repository for simple Value operations.
 * Provides core CRUD operations for Redis String (Value) data structure.
 *
 * @param <V> The value type
 */
@Slf4j
public abstract class BaseRedisValueRepository<V> extends BaseRedisRepository<V> {

    protected BaseRedisValueRepository(RedisTemplate<String, Object> redisTemplate, String keyPrefix) {
        super(redisTemplate, keyPrefix);
    }

    /**
     * Set a value in Redis
     *
     * @param key The Redis key
     * @param value The value to set
     */
    public void set(String key, V value) {
        try {
            redisTemplate.opsForValue().set(generateKey(key), value);
            log.debug("Set value in Redis: key={}", key);
        } catch (Exception e) {
            log.error("Error setting value in Redis: key={}, error={}", key, e.getMessage(), e);
        }
    }

    /**
     * Set a value in Redis with expiration
     *
     * @param key The Redis key
     * @param value The value to set
     * @param timeout The timeout value
     * @param unit The time unit
     */
    public void set(String key, V value, long timeout, TimeUnit unit) {
        try {
            redisTemplate.opsForValue().set(generateKey(key), value, timeout, unit);
            log.debug("Set value with TTL in Redis: key={}, timeout={}, unit={}", key, timeout, unit);
        } catch (Exception e) {
            log.error("Error setting value with TTL in Redis: key={}, error={}", key, e.getMessage(), e);
        }
    }

    /**
     * Set a value in Redis with expiration (Duration)
     *
     * @param key The Redis key
     * @param value The value to set
     * @param duration The duration
     */
    public void set(String key, V value, Duration duration) {
        try {
            redisTemplate.opsForValue().set(generateKey(key), value, duration);
            log.debug("Set value with duration in Redis: key={}, duration={}", key, duration);
        } catch (Exception e) {
            log.error("Error setting value with duration in Redis: key={}, error={}", key, e.getMessage(), e);
        }
    }

    /**
     * Set a value only if the key doesn't exist
     *
     * @param key The Redis key
     * @param value The value to set
     * @return true if the value was set, false otherwise
     */
    public boolean setIfAbsent(String key, V value) {
        try {
            Boolean result = redisTemplate.opsForValue().setIfAbsent(generateKey(key), value);
            log.debug("Set if absent in Redis: key={}, success={}", key, result);
            return Boolean.TRUE.equals(result);
        } catch (Exception e) {
            log.error("Error setting value if absent in Redis: key={}, error={}", key, e.getMessage(), e);
            return false;
        }
    }

    /**
     * Set a value only if the key doesn't exist, with expiration
     *
     * @param key The Redis key
     * @param value The value to set
     * @param timeout The timeout value
     * @param unit The time unit
     * @return true if the value was set, false otherwise
     */
    public boolean setIfAbsent(String key, V value, long timeout, TimeUnit unit) {
        try {
            Boolean result = redisTemplate.opsForValue().setIfAbsent(generateKey(key), value, timeout, unit);
            log.debug("Set if absent with TTL in Redis: key={}, success={}", key, result);
            return Boolean.TRUE.equals(result);
        } catch (Exception e) {
            log.error("Error setting value if absent with TTL in Redis: key={}, error={}", key, e.getMessage(), e);
            return false;
        }
    }

    /**
     * Set a value only if the key exists
     *
     * @param key The Redis key
     * @param value The value to set
     * @return true if the value was set, false otherwise
     */
    public boolean setIfPresent(String key, V value) {
        try {
            Boolean result = redisTemplate.opsForValue().setIfPresent(generateKey(key), value);
            log.debug("Set if present in Redis: key={}, success={}", key, result);
            return Boolean.TRUE.equals(result);
        } catch (Exception e) {
            log.error("Error setting value if present in Redis: key={}, error={}", key, e.getMessage(), e);
            return false;
        }
    }

    /**
     * Set a value only if the key exists, with expiration
     *
     * @param key The Redis key
     * @param value The value to set
     * @param timeout The timeout value
     * @param unit The time unit
     * @return true if the value was set, false otherwise
     */
    public boolean setIfPresent(String key, V value, long timeout, TimeUnit unit) {
        try {
            Boolean result = redisTemplate.opsForValue().setIfPresent(generateKey(key), value, timeout, unit);
            log.debug("Set if present with TTL in Redis: key={}, success={}", key, result);
            return Boolean.TRUE.equals(result);
        } catch (Exception e) {
            log.error("Error setting value if present with TTL in Redis: key={}, error={}", key, e.getMessage(), e);
            return false;
        }
    }

    /**
     * Get a value from Redis
     *
     * @param key The Redis key
     * @return The value, or null if not found
     */
    public Object get(String key) {
        try {
            Object value = redisTemplate.opsForValue().get(generateKey(key));
            log.debug("Retrieved value from Redis: key={}", key);
            return value;
        } catch (Exception e) {
            log.error("Error getting value from Redis: key={}, error={}", key, e.getMessage(), e);
            return null;
        }
    }

    /**
     * Get and set a value atomically
     *
     * @param key The Redis key
     * @param value The new value
     * @return The old value
     */
    public Object getAndSet(String key, V value) {
        try {
            Object oldValue = redisTemplate.opsForValue().getAndSet(generateKey(key), value);
            log.debug("Get and set value in Redis: key={}", key);
            return oldValue;
        } catch (Exception e) {
            log.error("Error getting and setting value in Redis: key={}, error={}", key, e.getMessage(), e);
            return null;
        }
    }

    /**
     * Increment a numeric value
     *
     * @param key The Redis key
     * @param delta The increment value
     * @return The value after increment
     */
    public Long increment(String key, long delta) {
        try {
            Long result = redisTemplate.opsForValue().increment(generateKey(key), delta);
            log.debug("Incremented value in Redis: key={}, delta={}, result={}", key, delta, result);
            return result;
        } catch (Exception e) {
            log.error("Error incrementing value in Redis: key={}, error={}", key, e.getMessage(), e);
            return null;
        }
    }

    /**
     * Increment a numeric value by 1
     *
     * @param key The Redis key
     * @return The value after increment
     */
    public Long increment(String key) {
        return increment(key, 1L);
    }

    /**
     * Decrement a numeric value
     *
     * @param key The Redis key
     * @param delta The decrement value
     * @return The value after decrement
     */
    public Long decrement(String key, long delta) {
        try {
            Long result = redisTemplate.opsForValue().decrement(generateKey(key), delta);
            log.debug("Decremented value in Redis: key={}, delta={}, result={}", key, delta, result);
            return result;
        } catch (Exception e) {
            log.error("Error decrementing value in Redis: key={}, error={}", key, e.getMessage(), e);
            return null;
        }
    }

    /**
     * Decrement a numeric value by 1
     *
     * @param key The Redis key
     * @return The value after decrement
     */
    public Long decrement(String key) {
        return decrement(key, 1L);
    }

    /**
     * Increment a floating point value
     *
     * @param key The Redis key
     * @param delta The increment value
     * @return The value after increment
     */
    public Double increment(String key, double delta) {
        try {
            Double result = redisTemplate.opsForValue().increment(generateKey(key), delta);
            log.debug("Incremented value (double) in Redis: key={}, delta={}, result={}", key, delta, result);
            return result;
        } catch (Exception e) {
            log.error("Error incrementing value (double) in Redis: key={}, error={}", key, e.getMessage(), e);
            return null;
        }
    }

    /**
     * Append a string value
     *
     * @param key The Redis key
     * @param value The value to append
     * @return The length of the string after append
     */
    public Integer append(String key, String value) {
        try {
            Integer result = redisTemplate.opsForValue().append(generateKey(key), value);
            log.debug("Appended to value in Redis: key={}, length={}", key, result);
            return result;
        } catch (Exception e) {
            log.error("Error appending to value in Redis: key={}, error={}", key, e.getMessage(), e);
            return null;
        }
    }
}
