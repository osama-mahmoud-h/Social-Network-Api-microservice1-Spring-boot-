package semsem.chatservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import semsem.chatservice.client.MainServiceClient;
import semsem.chatservice.dto.response.AppUserForChatDto;
import semsem.chatservice.dto.response.MyApiResponse;
import semsem.chatservice.service.FriendsService;

@Service
@RequiredArgsConstructor
@Slf4j
public class FriendsServiceImpl implements FriendsService {

    private final MainServiceClient mainServiceClient;

    @Override
    public Page<AppUserForChatDto> getFriendsPaginated(String token, int page, int size) {
        try {
            log.debug("Fetching paginated friends list from main-service: page={}, size={}", page, size);

            // Call main-service to get friends
            MyApiResponse<Page<AppUserForChatDto>> response = mainServiceClient.getFriendsPaginated(token, page, size);

            System.out.println("Response from main-service: " + response);
            log.info("Response from main-service: {}", response.getData().getContent());

            //TODO: handle isSuccess properly from main-service (now keeps returning false always)
            if (!response.isSuccess()) {
                log.info("Successfully retrieved {} friends from main-service",
                        response.getData() != null ? response.getData().getContent().size() : 0);
                return response.getData();
            } else {
                log.error("Failed to retrieve friends from main-service: {}", response.getMessage());
                throw new RuntimeException("Failed to retrieve friends: " + response.getMessage());
            }
        } catch (Exception e) {
            log.error("Error calling main-service to get friends: {}", e.getMessage(), e);
            throw new RuntimeException("Error retrieving friends list", e);
        }
    }

    @Override
    public boolean areFriends(String token, Long userId1, Long userId2) {
        try {
            log.info("Checking friendship between userId1={} and userId2={}", userId1, userId2);

//            // Call main-service to check friendship status
//            MyApiResponse<Boolean> response = mainServiceClient.areFriends(token, userId1, userId2);
//
//            if (response.isSuccess()) {
//                boolean areFriends = response.getData() != null && response.getData();
//                log.debug("Friendship check result: {}", areFriends);
//                return areFriends;
//            } else {
//                log.error("Failed to check friendship status: {}", response.getMessage());
//                return false;
//            }
            return true; // TODO: Temporarily bypassing friendship check
        } catch (Exception e) {
            log.error("Error calling main-service to check friendship: {}", e.getMessage(), e);
            return false;
        }
    }
}