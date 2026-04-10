package semsem.searchservice.service;


import semsem.searchservice.dto.response.AppUserResponseDto;
import semsem.searchservice.model.AppUserIndex;

import java.util.Set;

public interface AppUserIndexService {
    Set<AppUserResponseDto> searchAppUser(String searchTerm, int size, int page);
    void upsert(AppUserIndex appUserIndex);
    void deleteByUserId(Long userId);
}
