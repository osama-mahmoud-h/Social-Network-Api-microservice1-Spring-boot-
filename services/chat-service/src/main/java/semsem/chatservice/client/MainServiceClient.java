package semsem.chatservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import semsem.chatservice.dto.response.AppUserForChatDto;
import semsem.chatservice.dto.response.MyApiResponse;

@Component
@FeignClient(name = "main-service", url = "${main-service.url}")
public interface MainServiceClient {
    @GetMapping("/api/v1/friendship/get-friends-paginated")
    MyApiResponse<Page<AppUserForChatDto>> getFriendsPaginated(
            @RequestHeader("Authorization") String token,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    );

    @GetMapping("/api/v1/friendship/are-friends")
    MyApiResponse<Boolean> areFriends(
            @RequestHeader("Authorization") String token,
            @RequestParam("userId1") Long userId1,
            @RequestParam("userId2") Long userId2
    );
}