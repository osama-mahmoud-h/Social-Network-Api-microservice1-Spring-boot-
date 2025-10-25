package com.app.server.controller.internal;

import com.app.server.repository.FriendshipServiceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Internal API endpoints for inter-service communication
 * Used by notification-service to fetch user friend lists
 *
 * Note: In production, this should be secured with service-to-service authentication
 * or exposed only through internal network/API gateway
 */
@RestController
@RequestMapping("/internal/api/friendships")
@RequiredArgsConstructor
@Slf4j
public class InternalFriendshipController {

    private final FriendshipServiceRepository friendshipServiceRepository;

    /**
     * Get list of friend IDs for a given user
     * Used by notification-service to determine who to notify
     *
     * @param userId The user ID whose friends to fetch
     * @return List of friend user IDs
     */
    @GetMapping("/{userId}/friend-ids")
    public ResponseEntity<List<Long>> getFriendIds(@PathVariable Long userId) {
        log.debug("Internal API: Fetching friend IDs for userId={}", userId);

        List<Long> friendIds = friendshipServiceRepository.findAcceptedFriendIds(userId);

        log.debug("Internal API: Found {} friends for userId={}", friendIds.size(), userId);

        return ResponseEntity.ok(friendIds);
    }
}
