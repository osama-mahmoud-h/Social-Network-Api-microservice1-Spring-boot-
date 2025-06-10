package semsem.searchservice.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import semsem.searchservice.dto.response.CommentIndexResponseDto;
import semsem.searchservice.mapper.CommentIndexMapper;
import semsem.searchservice.model.CommentIndex;
import semsem.searchservice.repository.CommentIndexRepository;
import semsem.searchservice.service.CommentIndexService;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentIndexServiceImpl implements CommentIndexService {
    private final CommentIndexRepository commentIndexRepository;
    private final CommentIndexMapper commentIndexMapper;
    private final ElasticsearchClient elasticsearchClient;

    @Override
    public Set<CommentIndexResponseDto> fullTextSearch(String searchTerm, int size, int page) {
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        return commentIndexRepository.fuzzyFullTextSearch(searchTerm, pageable)
                .stream()
                .map(commentIndexMapper::mapCommentIndexToCommentIndexResponseDto)
                .collect(Collectors.toSet());
    }

    @Override
    public String save(CommentIndex commentIndex) {
        try {

            IndexResponse response = elasticsearchClient.index(i -> i
                    .index("comment_index")
                    .document(commentIndex)
            );
            System.out.println("Comment saved successfully: " + response);
            return response.id();
        } catch (Exception e) {
            System.out.println("Error saving comment: " + e.getMessage());
            throw new RuntimeException("Error saving comment: " + e.getMessage(), e);
        }
    }
}
