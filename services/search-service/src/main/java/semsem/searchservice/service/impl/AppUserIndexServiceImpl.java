package semsem.searchservice.service.impl;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import semsem.searchservice.dto.response.AppUserResponseDto;
import semsem.searchservice.mapper.AppUserIndexMapper;
import semsem.searchservice.repository.AppUserIndexRepository;
import semsem.searchservice.service.AppUserIndexService;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppUserIndexServiceImpl implements AppUserIndexService {
    private final AppUserIndexRepository appUserIndexRepository;
    private final AppUserIndexMapper appUserIndexMapper;

    @Override
    public Set<AppUserResponseDto> searchAppUser(String searchTerm, int size, int page) {
        Pageable pageable = Pageable.ofSize(size).withPage(page);

        return appUserIndexRepository.fuzzyFullTextSearch(searchTerm, pageable)
                .stream()
                .map(appUserIndexMapper::mapAppUserIndexToAppUserResponseDto)
                .collect(Collectors.toSet());
    }


}
