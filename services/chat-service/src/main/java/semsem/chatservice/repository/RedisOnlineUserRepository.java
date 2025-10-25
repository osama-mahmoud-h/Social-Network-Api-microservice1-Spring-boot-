package semsem.chatservice.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import semsem.chatservice.repository.base.BaseRedisSetRepository;
import semsem.chatservice.repository.base.BaseRedisValueRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Repository
@Slf4j
public class RedisOnlineUserRepository {

    private static final String ONLINE_USERS_KEY = "chat:online:users";
    private static final String USER_SESSION_PREFIX = "chat:user:session:";
    private static final long USER_SESSION_TTL_HOURS = 24; // User session expires after 24 hours

    private final OnlineUsersSetRepository onlineUsersSetRepository;
    private final UserSessionValueRepository userSessionValueRepository;

    public RedisOnlineUserRepository(RedisTemplate<String, Object> redisTemplate) {
        // For online users set, we don't use a prefix per key since it's a single global set
        // We'll override the key generation logic
        this.onlineUsersSetRepository = new OnlineUsersSetRepository(redisTemplate, "");
        this.userSessionValueRepository = new UserSessionValueRepository(redisTemplate, USER_SESSION_PREFIX);
    }

    // ============================================================================
    // Business Logic Methods - Online User Management
    // ============================================================================

    /**
     * Add a user to the online users set
     *
     * @param userId User ID
     */
    public void addOnlineUser(String userId) {
        try {
            onlineUsersSetRepository.addToGlobalSet(userId);
            userSessionValueRepository.set(userId, System.currentTimeMillis(), USER_SESSION_TTL_HOURS, TimeUnit.HOURS);
            log.info("User {} marked as online in Redis", userId);
        } catch (Exception e) {
            log.error("Error adding online user to Redis: {}", e.getMessage(), e);
        }
    }

    /**
     * Remove a user from the online users set
     *
     * @param userId User ID
     */
    public void removeOnlineUser(String userId) {
        try {
            onlineUsersSetRepository.removeFromGlobalSet(userId);
            userSessionValueRepository.delete(userId);
            log.info("User {} removed from online users in Redis", userId);
        } catch (Exception e) {
            log.error("Error removing online user from Redis: {}", e.getMessage(), e);
        }
    }

    /**
     * Get all online user IDs
     *
     * @return Set of online user IDs
     */
    public Set<String> getAllOnlineUsers() {
        try {
            Set<Object> members = onlineUsersSetRepository.getGlobalSetMembers();
            if (members.isEmpty()) {
                log.info("No online users found in Redis");
                return new HashSet<>();
            }

            Set<String> onlineUsers = new HashSet<>();
            for (Object member : members) {
                if (member != null) {
                    onlineUsers.add(member.toString());
                }
            }

            log.info("Retrieved {} online users from Redis", onlineUsers.size());
            return onlineUsers;
        } catch (Exception e) {
            log.error("Error retrieving online users from Redis: {}", e.getMessage(), e);
            return new HashSet<>();
        }
    }

    /**
     * Check if a user is online
     *
     * @param userId User ID
     * @return true if user is online, false otherwise
     */
    public boolean isUserOnline(String userId) {
        try {
            return onlineUsersSetRepository.isInGlobalSet(userId);
        } catch (Exception e) {
            log.error("Error checking if user is online in Redis: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Filter online users from a given set of user IDs
     *
     * @param userIds Set of user IDs to check
     * @return Set of online user IDs
     */
    public Set<String> filterOnlineUsers(List<String> userIds) {
        Set<String> onlineUsers = new HashSet<>();
        try {
            userIds.forEach(userId -> {
                if (isUserOnline(userId)) {
                    onlineUsers.add(userId);
                }
            });
            log.info("Filtered {} online users from provided set", onlineUsers.size());
        } catch (Exception e) {
            log.error("Error filtering online users from Redis: {}", e.getMessage(), e);
        }
        return onlineUsers;
    }

    /**
     * Get count of online users
     *
     * @return Number of online users
     */
    public long getOnlineUserCount() {
        try {
            return onlineUsersSetRepository.getGlobalSetSize();
        } catch (Exception e) {
            log.error("Error getting online user count from Redis: {}", e.getMessage(), e);
            return 0;
        }
    }

    /**
     * Refresh user session TTL
     *
     * @param userId User ID
     */
    public void refreshUserSession(String userId) {
        try {
            if (userSessionValueRepository.exists(userId)) {
                userSessionValueRepository.refreshTTL(userId, USER_SESSION_TTL_HOURS, TimeUnit.HOURS);
                log.info("Refreshed session TTL for user {}", userId);
            }
        } catch (Exception e) {
            log.error("Error refreshing user session TTL: {}", e.getMessage(), e);
        }
    }

    // ============================================================================
    // Inner Repository Classes - Core CRUD Operations Delegation
    // ============================================================================

    /**
     * Repository for online users set operations
     * Extends BaseRedisSetRepository to inherit core Set CRUD operations
     */
    private static class OnlineUsersSetRepository extends BaseRedisSetRepository<String> {
        public OnlineUsersSetRepository(RedisTemplate<String, Object> redisTemplate, String keyPrefix) {
            super(redisTemplate, keyPrefix);
        }

        /**
         * Add a user to the global online users set
         */
        public void addToGlobalSet(String userId) {
            add(ONLINE_USERS_KEY, userId);
        }

        /**
         * Remove a user from the global online users set
         */
        public void removeFromGlobalSet(String userId) {
            remove(ONLINE_USERS_KEY, userId);
        }

        /**
         * Check if a user is in the global online users set
         */
        public boolean isInGlobalSet(String userId) {
            return isMember(ONLINE_USERS_KEY, userId);
        }

        /**
         * Get all members of the global online users set
         */
        public Set<Object> getGlobalSetMembers() {
            return members(ONLINE_USERS_KEY);
        }

        /**
         * Get the size of the global online users set
         */
        public long getGlobalSetSize() {
            return size(ONLINE_USERS_KEY);
        }

        @Override
        protected String generateKey(String identifier) {
            // For the global set, we don't use the prefix
            return identifier;
        }
    }

    /**
     * Repository for user session value operations
     * Extends BaseRedisValueRepository to inherit core Value CRUD operations
     */
    private static class UserSessionValueRepository extends BaseRedisValueRepository<Long> {
        public UserSessionValueRepository(RedisTemplate<String, Object> redisTemplate, String keyPrefix) {
            super(redisTemplate, keyPrefix);
        }
    }
}
