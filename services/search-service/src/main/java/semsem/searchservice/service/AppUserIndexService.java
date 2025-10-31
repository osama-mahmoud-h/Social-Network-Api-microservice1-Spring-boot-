package semsem.searchservice.service;


import semsem.searchservice.dto.response.AppUserResponseDto;

import java.util.Set;

public interface AppUserIndexService {
    Set<AppUserResponseDto> searchAppUser(String searchTerm, int size, int page);
    String save(semsem.searchservice.model.AppUserIndex appUserIndex);
    void update(semsem.searchservice.model.AppUserIndex appUserIndex);
}
