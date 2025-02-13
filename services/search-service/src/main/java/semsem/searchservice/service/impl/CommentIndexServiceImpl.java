package semsem.searchservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import semsem.searchservice.dto.response.CommentIndexResponseDto;
import semsem.searchservice.mapper.CommentIndexMapper;
import semsem.searchservice.repository.CommentIndexRepository;
import semsem.searchservice.service.CommentIndexService;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentIndexServiceImpl implements CommentIndexService {
    private final CommentIndexRepository commentIndexRepository;
    private final CommentIndexMapper commentIndexMapper;

    @Override
    public Set<CommentIndexResponseDto> fullTextSearch(String searchTerm, int size, int page) {
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        return commentIndexRepository.fuzzyFullTextSearch(searchTerm, pageable)
                .stream()
                .map(commentIndexMapper::mapCommentIndexToCommentIndexResponseDto)
                .collect(Collectors.toSet());
    }
}
