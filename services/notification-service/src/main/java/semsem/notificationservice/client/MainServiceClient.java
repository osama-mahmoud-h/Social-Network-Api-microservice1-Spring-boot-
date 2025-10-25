package semsem.notificationservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * Feign client for communicating with main-service
 * Fetches friend relationships for notification fan-out
 */
@FeignClient(
    name = "main-service",
    url = "${services.main-service.url:http://localhost:8083}"
)
public interface MainServiceClient {

    /**
     * Get list of friend IDs for a user
     *
     * @param userId User ID
     * @return List of friend IDs
     */
    @GetMapping("/internal/api/friendships/{userId}/friend-ids")
    List<Long> getFriendIds(@PathVariable("userId") Long userId);
}
