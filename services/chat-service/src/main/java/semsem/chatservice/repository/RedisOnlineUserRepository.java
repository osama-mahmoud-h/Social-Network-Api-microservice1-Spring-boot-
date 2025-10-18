package semsem.chatservice.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
@Slf4j
public class RedisOnlineUserRepository {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String ONLINE_USERS_KEY = "chat:online:users";
    private static final String USER_SESSION_PREFIX = "chat:user:session:";
    private static final long USER_SESSION_TTL_HOURS = 24; // User session expires after 24 hours

    /**
     * Add a user to the online users set
     * @param userId User ID
     */
    public void addOnlineUser(String userId) {
        try {
            redisTemplate.opsForSet().add(ONLINE_USERS_KEY, userId);
            // Set user session with TTL
            String sessionKey = USER_SESSION_PREFIX + userId;
            redisTemplate.opsForValue().set(sessionKey, System.currentTimeMillis(), USER_SESSION_TTL_HOURS, TimeUnit.HOURS);
            log.info("User {} marked as online in Redis", userId);
        } catch (Exception e) {
            log.error("Error adding online user to Redis: {}", e.getMessage(), e);
        }
    }

    /**
     * Remove a user from the online users set
     * @param userId User ID
     */
    public void removeOnlineUser(String userId) {
        try {
            redisTemplate.opsForSet().remove(ONLINE_USERS_KEY, userId);
            // Remove user session
            String sessionKey = USER_SESSION_PREFIX + userId;
            redisTemplate.delete(sessionKey);
            log.info("User {} removed from online users in Redis", userId);
        } catch (Exception e) {
            log.error("Error removing online user from Redis: {}", e.getMessage(), e);
        }
    }

    /**
     * Get all online user IDs
     * @return Set of online user IDs
     */
    public Set<String> getAllOnlineUsers() {
        try {
            Set<Object> members = redisTemplate.opsForSet().members(ONLINE_USERS_KEY);
            if (members == null || members.isEmpty()) {
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
     * @param userId User ID
     * @return true if user is online, false otherwise
     */
    public boolean isUserOnline(String userId) {
        try {
            Boolean isMember = redisTemplate.opsForSet().isMember(ONLINE_USERS_KEY, userId);
            return Boolean.TRUE.equals(isMember);
        } catch (Exception e) {
            log.error("Error checking if user is online in Redis: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Get count of online users
     * @return Number of online users
     */
    public long getOnlineUserCount() {
        try {
            Long size = redisTemplate.opsForSet().size(ONLINE_USERS_KEY);
            return size != null ? size : 0;
        } catch (Exception e) {
            log.error("Error getting online user count from Redis: {}", e.getMessage(), e);
            return 0;
        }
    }

    /**
     * Refresh user session TTL
     * @param userId User ID
     */
    public void refreshUserSession(String userId) {
        try {
            String sessionKey = USER_SESSION_PREFIX + userId;
            if (Boolean.TRUE.equals(redisTemplate.hasKey(sessionKey))) {
                redisTemplate.expire(sessionKey, USER_SESSION_TTL_HOURS, TimeUnit.HOURS);
                log.info("Refreshed session TTL for user {}", userId);
            }
        } catch (Exception e) {
            log.error("Error refreshing user session TTL: {}", e.getMessage(), e);
        }
    }
}