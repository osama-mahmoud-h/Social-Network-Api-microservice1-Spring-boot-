package semsem.searchservice.service.impl;


import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import semsem.searchservice.dto.response.AppUserResponseDto;
import semsem.searchservice.mapper.AppUserIndexMapper;
import semsem.searchservice.model.AppUserIndex;
import semsem.searchservice.repository.AppUserIndexRepository;
import semsem.searchservice.service.AppUserIndexService;

import java.util.Optional;
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
    public String save(AppUserIndex appUserIndex) {
        try {
            IndexResponse response = elasticsearchClient.index(i -> i
                    .index("app_user_index")
                    .document(appUserIndex)
            );
            System.out.println("User saved successfully: " + response);
            return response.id();
        } catch (Exception e) {
            System.out.println("Error saving user: " + e.getMessage());
            throw new RuntimeException("Error saving user: " + e.getMessage(), e);
        }
    }

    @Override
    public void update(AppUserIndex appUserIndex) {
        try {
            // Find the existing document by userId - we need to add this query method
            Optional<AppUserIndex> existingUser = appUserIndexRepository.findById(appUserIndex.getId());

            if (existingUser.isEmpty()) {
                // If not found by ID, try to find by userId
                System.out.println("User not found with id: " + appUserIndex.getId() + ", creating new index");
                save(appUserIndex);
                return;
            }

            // Update the document
            elasticsearchClient.index(i -> i
                    .index("app_user_index")
                    .id(appUserIndex.getId())
                    .document(appUserIndex)
            );

            System.out.println("User updated successfully with ID: " + appUserIndex.getId());
        } catch (Exception e) {
            System.out.println("Error updating user: " + e.getMessage());
            throw new RuntimeException("Error updating user: " + e.getMessage(), e);
        }
    }


}
