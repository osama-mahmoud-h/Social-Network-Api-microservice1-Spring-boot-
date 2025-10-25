package semsem.chatservice.repository.base;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.HashSet;
import java.util.Set;

/**
 * Base Redis Repository for Set operations.
 * Provides core CRUD operations for Redis Set data structure.
 *
 * @param <T> The type of elements stored in the set
 */
@Slf4j
public abstract class BaseRedisSetRepository<T> extends BaseRedisRepository<T> {

    protected BaseRedisSetRepository(RedisTemplate<String, Object> redisTemplate, String keyPrefix) {
        super(redisTemplate, keyPrefix);
    }

    /**
     * Add one or more members to a set
     *
     * @param key The Redis key
     * @param values The values to add
     * @return The number of elements added to the set
     */
    @SafeVarargs
    public final Long add(String key, T... values) {
        try {
            Long count = redisTemplate.opsForSet().add(generateKey(key), (Object[]) values);
            log.debug("Added {} members to set: key={}", count, key);
            return count;
        } catch (Exception e) {
            log.error("Error adding members to set: key={}, error={}", key, e.getMessage(), e);
            return 0L;
        }
    }

    /**
     * Remove one or more members from a set
     *
     * @param key The Redis key
     * @param values The values to remove
     * @return The number of elements removed from the set
     */
    public Long remove(String key, Object... values) {
        try {
            Long count = redisTemplate.opsForSet().remove(generateKey(key), values);
            log.debug("Removed {} members from set: key={}", count, key);
            return count;
        } catch (Exception e) {
            log.error("Error removing members from set: key={}, error={}", key, e.getMessage(), e);
            return 0L;
        }
    }

    /**
     * Check if a value is a member of a set
     *
     * @param key The Redis key
     * @param value The value to check
     * @return true if the value is a member, false otherwise
     */
    public boolean isMember(String key, Object value) {
        try {
            Boolean result = redisTemplate.opsForSet().isMember(generateKey(key), value);
            return Boolean.TRUE.equals(result);
        } catch (Exception e) {
            log.error("Error checking set membership: key={}, error={}", key, e.getMessage(), e);
            return false;
        }
    }

    /**
     * Get all members of a set
     *
     * @param key The Redis key
     * @return All members of the set
     */
    public Set<Object> members(String key) {
        try {
            Set<Object> members = redisTemplate.opsForSet().members(generateKey(key));
            log.debug("Retrieved {} members from set: key={}", members != null ? members.size() : 0, key);
            return members != null ? members : new HashSet<>();
        } catch (Exception e) {
            log.error("Error getting set members: key={}, error={}", key, e.getMessage(), e);
            return new HashSet<>();
        }
    }

    /**
     * Get the size of a set
     *
     * @param key The Redis key
     * @return The number of elements in the set
     */
    public long size(String key) {
        try {
            Long size = redisTemplate.opsForSet().size(generateKey(key));
            return size != null ? size : 0L;
        } catch (Exception e) {
            log.error("Error getting set size: key={}, error={}", key, e.getMessage(), e);
            return 0L;
        }
    }

    /**
     * Pop a random member from a set
     *
     * @param key The Redis key
     * @return A random member from the set
     */
    public Object pop(String key) {
        try {
            Object value = redisTemplate.opsForSet().pop(generateKey(key));
            log.debug("Popped random member from set: key={}", key);
            return value;
        } catch (Exception e) {
            log.error("Error popping member from set: key={}, error={}", key, e.getMessage(), e);
            return null;
        }
    }

    /**
     * Pop multiple random members from a set
     *
     * @param key The Redis key
     * @param count The number of members to pop
     * @return A list of random members from the set
     */
    public Set<Object> pop(String key, long count) {
        try {
            Set<Object> members = new HashSet<>();
            for (long i = 0; i < count; i++) {
                Object member = pop(key);
                if (member != null) {
                    members.add(member);
                } else {
                    break;
                }
            }
            log.debug("Popped {} random members from set: key={}", members.size(), key);
            return members;
        } catch (Exception e) {
            log.error("Error popping members from set: key={}, error={}", key, e.getMessage(), e);
            return new HashSet<>();
        }
    }

    /**
     * Get random members from a set without removing them
     *
     * @param key The Redis key
     * @param count The number of random members to get
     * @return A list of random members from the set
     */
    public Set<Object> randomMembers(String key, long count) {
        try {
            java.util.List<Object> members = redisTemplate.opsForSet().randomMembers(generateKey(key), count);
            log.debug("Retrieved {} random members from set: key={}", members != null ? members.size() : 0, key);
            return members != null ? new HashSet<>(members) : new HashSet<>();
        } catch (Exception e) {
            log.error("Error getting random members from set: key={}, error={}", key, e.getMessage(), e);
            return new HashSet<>();
        }
    }

    /**
     * Move a member from one set to another
     *
     * @param sourceKey The source set key
     * @param value The value to move
     * @param destKey The destination set key
     * @return true if the move was successful, false otherwise
     */
    public boolean move(String sourceKey, Object value, String destKey) {
        try {
            Boolean result = redisTemplate.opsForSet().move(generateKey(sourceKey), value, generateKey(destKey));
            log.debug("Moved member from {} to {}: success={}", sourceKey, destKey, result);
            return Boolean.TRUE.equals(result);
        } catch (Exception e) {
            log.error("Error moving member between sets: source={}, dest={}, error={}",
                     sourceKey, destKey, e.getMessage(), e);
            return false;
        }
    }

    /**
     * Get the intersection of multiple sets
     *
     * @param key The first set key
     * @param otherKeys Other set keys
     * @return The intersection of all sets
     */
    public Set<Object> intersect(String key, String... otherKeys) {
        try {
            String[] fullKeys = new String[otherKeys.length];
            for (int i = 0; i < otherKeys.length; i++) {
                fullKeys[i] = generateKey(otherKeys[i]);
            }
            Set<Object> intersection = redisTemplate.opsForSet().intersect(generateKey(key), java.util.Arrays.asList(fullKeys));
            log.debug("Calculated intersection of sets: count={}", intersection != null ? intersection.size() : 0);
            return intersection != null ? intersection : new HashSet<>();
        } catch (Exception e) {
            log.error("Error calculating set intersection: key={}, error={}", key, e.getMessage(), e);
            return new HashSet<>();
        }
    }

    /**
     * Get the union of multiple sets
     *
     * @param key The first set key
     * @param otherKeys Other set keys
     * @return The union of all sets
     */
    public Set<Object> union(String key, String... otherKeys) {
        try {
            String[] fullKeys = new String[otherKeys.length];
            for (int i = 0; i < otherKeys.length; i++) {
                fullKeys[i] = generateKey(otherKeys[i]);
            }
            Set<Object> union = redisTemplate.opsForSet().union(generateKey(key), java.util.Arrays.asList(fullKeys));
            log.debug("Calculated union of sets: count={}", union != null ? union.size() : 0);
            return union != null ? union : new HashSet<>();
        } catch (Exception e) {
            log.error("Error calculating set union: key={}, error={}", key, e.getMessage(), e);
            return new HashSet<>();
        }
    }

    /**
     * Get the difference between sets
     *
     * @param key The first set key
     * @param otherKeys Other set keys
     * @return The difference between sets
     */
    public Set<Object> difference(String key, String... otherKeys) {
        try {
            String[] fullKeys = new String[otherKeys.length];
            for (int i = 0; i < otherKeys.length; i++) {
                fullKeys[i] = generateKey(otherKeys[i]);
            }
            Set<Object> diff = redisTemplate.opsForSet().difference(generateKey(key), java.util.Arrays.asList(fullKeys));
            log.debug("Calculated difference of sets: count={}", diff != null ? diff.size() : 0);
            return diff != null ? diff : new HashSet<>();
        } catch (Exception e) {
            log.error("Error calculating set difference: key={}, error={}", key, e.getMessage(), e);
            return new HashSet<>();
        }
    }
}
