package semsem.searchservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import semsem.searchservice.dto.response.PostIndexResponseDto;
import semsem.searchservice.mapper.PostIndexMapper;
import semsem.searchservice.model.PostIndex;
import semsem.searchservice.repository.PostIndexRepository;
import semsem.searchservice.service.PostIndexService;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostIndexServiceImpl implements PostIndexService {
    private final PostIndexRepository postIndexRepository;
    private final PostIndexMapper postIndexMapper;

    // ✅ Full-text fuzzy search across multiple fields
    @Override
    public Set<PostIndexResponseDto> fuzzyFullTextSearch(String keyword, int size, int page) {
        Pageable pageable = Pageable.ofSize(size).withPage(page);

        return postIndexRepository.fuzzyFullTextSearch(keyword, pageable).stream()
                .map(postIndexMapper::mapPostIndexToPostIndexResponseDto)
                .collect(Collectors.toSet());
    }

    @Override
    public void save(PostIndex postIndex) {
        postIndexRepository.save(postIndex);
    }

}
