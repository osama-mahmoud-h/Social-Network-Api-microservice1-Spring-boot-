package semsem.searchservice.service;


import semsem.searchservice.dto.response.AppUserResponseDto;

import java.util.Set;

public interface AppUserIndexService {
    Set<AppUserResponseDto> searchAppUser(String searchTerm, int size, int page);
}
