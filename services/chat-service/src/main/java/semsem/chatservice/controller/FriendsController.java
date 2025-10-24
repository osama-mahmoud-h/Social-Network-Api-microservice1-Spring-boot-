package semsem.chatservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import semsem.chatservice.dto.response.AppUserForChatDto;
import semsem.chatservice.service.FriendsService;

@RestController
@RequestMapping("/api/friends")
@RequiredArgsConstructor
@Slf4j
public class FriendsController {

    private final FriendsService friendsService;

    /**
     * Get paginated list of friends for the authenticated user
     * This endpoint calls main-service to fetch friends and returns them for chat functionality
     */
    @GetMapping("/paginated")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Page<AppUserForChatDto>> getFriendsPaginated(
            @RequestHeader("Authorization") String token,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        log.info("Fetching paginated friends: page={}, size={}", page, size);

        Page<AppUserForChatDto> friends = friendsService.getFriendsPaginated(token, page, size);

        log.debug("Returning {} friends for page {}", friends.getContent().size(), page);
        return ResponseEntity.ok(friends);
    }
}