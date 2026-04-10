package semsem.searchservice.service.impl;


import co.elastic.clients.elasticsearch.ElasticsearchClient;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import semsem.searchservice.dto.response.AppUserResponseDto;
import semsem.searchservice.mapper.AppUserIndexMapper;
import semsem.searchservice.model.AppUserIndex;
import semsem.searchservice.repository.AppUserIndexRepository;
import semsem.searchservice.service.AppUserIndexService;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppUserIndexServiceImpl implements AppUserIndexService {
    private final AppUserIndexRepository appUserIndexRepository;
    private final AppUserIndexMapper appUserIndexMapper;
    private final ElasticsearchClient elasticsearchClient;

    @Override
    public Set<AppUserResponseDto> searchAppUser(String searchTerm, int size, int page) {
        Pageable pageable = Pageable.ofSize(size).withPage(page);

        return appUserIndexRepository.fuzzyFullTextSearch(searchTerm, pageable)
                .stream()
                .map(appUserIndexMapper::mapAppUserIndexToAppUserResponseDto)
                .collect(Collectors.toSet());
    }

    @Override
    public void upsert(AppUserIndex appUserIndex) {
        try {
            elasticsearchClient.index(i -> i
                    .index("app_user_index")
                    .id(appUserIndex.getId()) // deterministic id -> idempotent upsert
                    .document(appUserIndex)
            );
        } catch (Exception e) {
            throw new RuntimeException("Error upserting user index: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteByUserId(Long userId) {
        appUserIndexRepository.deleteById(userId.toString());
    }


}
