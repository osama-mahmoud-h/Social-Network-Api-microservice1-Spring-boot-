package semsem.chatservice.repository.base;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Base Redis Repository for Hash operations.
 * Provides core CRUD operations for Redis Hash data structure.
 *
 * @param <HK> The hash key type
 * @param <HV> The hash value type
 */
@Slf4j
public abstract class BaseRedisHashRepository<HK, HV> extends BaseRedisRepository<HV> {

    protected BaseRedisHashRepository(RedisTemplate<String, Object> redisTemplate, String keyPrefix) {
        super(redisTemplate, keyPrefix);
    }

    /**
     * Set a value in a hash
     *
     * @param key The Redis key
     * @param hashKey The hash key
     * @param value The value to set
     */
    public void put(String key, HK hashKey, HV value) {
        try {
            redisTemplate.opsForHash().put(generateKey(key), hashKey, value);
            log.debug("Put value in hash: key={}, hashKey={}", key, hashKey);
        } catch (Exception e) {
            log.error("Error putting value in hash: key={}, hashKey={}, error={}", key, hashKey, e.getMessage(), e);
        }
    }

    /**
     * Set multiple values in a hash
     *
     * @param key The Redis key
     * @param map The map of hash keys and values
     */
    public void putAll(String key, Map<HK, HV> map) {
        try {
            Map<Object, Object> objectMap = new HashMap<>(map);
            redisTemplate.opsForHash().putAll(generateKey(key), objectMap);
            log.debug("Put {} entries in hash: key={}", map.size(), key);
        } catch (Exception e) {
            log.error("Error putting multiple values in hash: key={}, error={}", key, e.getMessage(), e);
        }
    }

    /**
     * Set a value in a hash only if the hash key doesn't exist
     *
     * @param key The Redis key
     * @param hashKey The hash key
     * @param value The value to set
     * @return true if the value was set, false otherwise
     */
    public boolean putIfAbsent(String key, HK hashKey, HV value) {
        try {
            Boolean result = redisTemplate.opsForHash().putIfAbsent(generateKey(key), hashKey, value);
            log.debug("Put if absent in hash: key={}, hashKey={}, success={}", key, hashKey, result);
            return Boolean.TRUE.equals(result);
        } catch (Exception e) {
            log.error("Error putting if absent in hash: key={}, hashKey={}, error={}",
                     key, hashKey, e.getMessage(), e);
            return false;
        }
    }

    /**
     * Get a value from a hash
     *
     * @param key The Redis key
     * @param hashKey The hash key
     * @return The value, or null if not found
     */
    public Object get(String key, HK hashKey) {
        try {
            Object value = redisTemplate.opsForHash().get(generateKey(key), hashKey);
            log.debug("Retrieved value from hash: key={}, hashKey={}", key, hashKey);
            return value;
        } catch (Exception e) {
            log.error("Error getting value from hash: key={}, hashKey={}, error={}", key, hashKey, e.getMessage(), e);
            return null;
        }
    }

    /**
     * Get multiple values from a hash
     *
     * @param key The Redis key
     * @param hashKeys The hash keys
     * @return List of values corresponding to the hash keys
     */
    public List<Object> multiGet(String key, List<HK> hashKeys) {
        try {
            List<Object> objectKeys = new java.util.ArrayList<>(hashKeys);
            List<Object> values = redisTemplate.opsForHash().multiGet(generateKey(key), objectKeys);
            log.debug("Retrieved {} values from hash: key={}", values.size(), key);
            return values;
        } catch (Exception e) {
            log.error("Error getting multiple values from hash: key={}, error={}", key, e.getMessage(), e);
            return new java.util.ArrayList<>();
        }
    }

    /**
     * Get all entries from a hash
     *
     * @param key The Redis key
     * @return Map of all hash entries
     */
    public Map<Object, Object> entries(String key) {
        try {
            Map<Object, Object> entries = redisTemplate.opsForHash().entries(generateKey(key));
            log.debug("Retrieved {} entries from hash: key={}", entries.size(), key);
            return entries;
        } catch (Exception e) {
            log.error("Error getting entries from hash: key={}, error={}", key, e.getMessage(), e);
            return new HashMap<>();
        }
    }

    /**
     * Get all keys from a hash
     *
     * @param key The Redis key
     * @return Set of all hash keys
     */
    public Set<Object> keys(String key) {
        try {
            Set<Object> keys = redisTemplate.opsForHash().keys(generateKey(key));
            log.debug("Retrieved {} keys from hash: key={}", keys.size(), key);
            return keys;
        } catch (Exception e) {
            log.error("Error getting keys from hash: key={}, error={}", key, e.getMessage(), e);
            return new java.util.HashSet<>();
        }
    }

    /**
     * Get all values from a hash
     *
     * @param key The Redis key
     * @return List of all hash values
     */
    public List<Object> values(String key) {
        try {
            List<Object> values = redisTemplate.opsForHash().values(generateKey(key));
            log.debug("Retrieved {} values from hash: key={}", values.size(), key);
            return values;
        } catch (Exception e) {
            log.error("Error getting values from hash: key={}, error={}", key, e.getMessage(), e);
            return new java.util.ArrayList<>();
        }
    }

    /**
     * Check if a hash key exists
     *
     * @param key The Redis key
     * @param hashKey The hash key
     * @return true if the hash key exists, false otherwise
     */
    public boolean hasKey(String key, HK hashKey) {
        try {
            Boolean result = redisTemplate.opsForHash().hasKey(generateKey(key), hashKey);
            return Boolean.TRUE.equals(result);
        } catch (Exception e) {
            log.error("Error checking hash key existence: key={}, hashKey={}, error={}",
                     key, hashKey, e.getMessage(), e);
            return false;
        }
    }

    /**
     * Delete one or more hash keys
     *
     * @param key The Redis key
     * @param hashKeys The hash keys to delete
     * @return The number of hash keys deleted
     */
    @SafeVarargs
    public final Long delete(String key, HK... hashKeys) {
        try {
            Long count = redisTemplate.opsForHash().delete(generateKey(key), (Object[]) hashKeys);
            log.debug("Deleted {} hash keys: key={}", count, key);
            return count;
        } catch (Exception e) {
            log.error("Error deleting hash keys: key={}, error={}", key, e.getMessage(), e);
            return 0L;
        }
    }

    /**
     * Get the size of a hash
     *
     * @param key The Redis key
     * @return The number of entries in the hash
     */
    public long size(String key) {
        try {
            Long size = redisTemplate.opsForHash().size(generateKey(key));
            return size != null ? size : 0L;
        } catch (Exception e) {
            log.error("Error getting hash size: key={}, error={}", key, e.getMessage(), e);
            return 0L;
        }
    }

    /**
     * Increment a hash value (numeric)
     *
     * @param key The Redis key
     * @param hashKey The hash key
     * @param delta The increment value
     * @return The value after increment
     */
    public Long increment(String key, HK hashKey, long delta) {
        try {
            Long result = redisTemplate.opsForHash().increment(generateKey(key), hashKey, delta);
            log.debug("Incremented hash value: key={}, hashKey={}, delta={}, result={}",
                     key, hashKey, delta, result);
            return result;
        } catch (Exception e) {
            log.error("Error incrementing hash value: key={}, hashKey={}, error={}",
                     key, hashKey, e.getMessage(), e);
            return null;
        }
    }

    /**
     * Increment a hash value (floating point)
     *
     * @param key The Redis key
     * @param hashKey The hash key
     * @param delta The increment value
     * @return The value after increment
     */
    public Double increment(String key, HK hashKey, double delta) {
        try {
            Double result = redisTemplate.opsForHash().increment(generateKey(key), hashKey, delta);
            log.debug("Incremented hash value (double): key={}, hashKey={}, delta={}, result={}",
                     key, hashKey, delta, result);
            return result;
        } catch (Exception e) {
            log.error("Error incrementing hash value (double): key={}, hashKey={}, error={}",
                     key, hashKey, e.getMessage(), e);
            return null;
        }
    }
}
