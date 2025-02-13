package semsem.searchservice.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.Operator;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.CompletionSuggestOption;
import co.elastic.clients.elasticsearch.core.search.PhraseSuggestOption;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import semsem.searchservice.enums.IndexType;
import semsem.searchservice.mapper.AppUserIndexMapper;
import semsem.searchservice.mapper.CommentIndexMapper;
import semsem.searchservice.mapper.PostIndexMapper;
import semsem.searchservice.model.AppUserIndex;
import semsem.searchservice.model.CommentIndex;
import semsem.searchservice.model.PostIndex;
import semsem.searchservice.repository.AppUserIndexRepository;
import semsem.searchservice.repository.CommentIndexRepository;
import semsem.searchservice.repository.PostIndexRepository;
import semsem.searchservice.service.SearchService;
import co.elastic.clients.elasticsearch.core.search.Hit;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {
    private final PostIndexMapper postIndexMapper;
    private final AppUserIndexMapper appUserIndexMapper;
    private final CommentIndexMapper commentIndexMapper;

    private final PostIndexRepository postIndexRepository;
    private final AppUserIndexRepository appUserIndexRepository;
    private final CommentIndexRepository commentIndexRepository;

    public record SearchResult<T>(List<T> results, long total) {}

    private final ElasticsearchClient elasticsearchClient;

    @Override
    public List<Object> searchAcrossIndices(String searchTerm, int size, int page) {
        int from = page * size;

        SearchResponse<Object> searchResponse = null;
        try {
            searchResponse = elasticsearchClient.search(s -> s
                            .index("post_index", "comment_index", "app_user_index")
                            .from(from)
                            .size(size)
                            .query(q -> q
                                    .multiMatch(m -> m
                                            .query(searchTerm)
                                            .fields("content", "firstName", "lastName", "email")
                                    )
                            ),
                    Object.class
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        List<Object> results = Collections.singletonList(searchResponse.hits().hits().stream()
                .map(Hit::source)
                .map(hit -> {
                    Map<String,Object> hitMap = (Map<String, Object>) hit;
                    String indexType = (String) hitMap.get("indexType");
                    System.out.println("indexType: " + indexType);
                    System.out.println("hit: " + hit);

                    if(indexType.equals(IndexType.POST_INDEX.name())) {
                        return postIndexMapper.mapDbObjectIndexToPostResponseDto(hitMap);
                    } else if(indexType.equals(IndexType.COMMENT_INDEX.name())) {
                        return commentIndexMapper.mapDbObjectIndexToCommentResponseDto(hitMap);
                    } else if(indexType.equals(IndexType.APP_USER_INDEX.name())) {
                        return appUserIndexMapper.mapDbObjectIndexToAppUserResponseDto(hitMap);
                    }
                    return null;
                })
               // .filter(Objects::nonNull)
                .collect(Collectors.toList()));

        long totalHits = searchResponse.hits().total().value(); // ✅ Get total number of matching documents
        System.out.println("Total hits: " + results);
       return results;
    }

    public Set<Object> searchAcrossMultiIndices(String searchTerm,IndexType searchCategory,  int size, int page){
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        if(searchCategory.equals(IndexType.POST_INDEX)){
            return this.postIndexRepository.fuzzyFullTextSearch(searchTerm,pageable).stream()
                    .map(postIndexMapper::mapPostIndexToPostIndexResponseDto)
                    .collect(Collectors.toSet());
        }else if(searchCategory.equals(IndexType.COMMENT_INDEX)){
            return this.commentIndexRepository.fuzzyFullTextSearch(searchTerm,pageable).stream()
                    .map(commentIndexMapper::mapCommentIndexToCommentIndexResponseDto)
                    .collect(Collectors.toSet());
        }else if(searchCategory.equals(IndexType.APP_USER_INDEX)){
            return this.appUserIndexRepository.fuzzyFullTextSearch(searchTerm,pageable).stream()
                    .map(appUserIndexMapper::mapAppUserIndexToAppUserResponseDto)
                    .collect(Collectors.toSet());

        }
        return Set.of();
    }

}

