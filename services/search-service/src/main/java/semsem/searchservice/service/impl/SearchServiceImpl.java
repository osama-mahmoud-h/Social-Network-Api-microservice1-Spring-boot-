package semsem.searchservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import semsem.searchservice.dto.request.SearchMultiIndexesRequestDto;
import semsem.searchservice.enums.IndexType;
import semsem.searchservice.mapper.AppUserIndexMapper;
import semsem.searchservice.mapper.CommentIndexMapper;
import semsem.searchservice.mapper.PostIndexMapper;
import semsem.searchservice.repository.AppUserIndexRepository;
import semsem.searchservice.repository.CommentIndexRepository;
import semsem.searchservice.repository.PostIndexRepository;
import semsem.searchservice.service.SearchService;

import java.util.*;
import java.util.function.BiFunction;
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

    private final Map<IndexType, BiFunction<String, Pageable, Set<?>>> searchStrategies = this.initSearchStrategies();

    @Override
    public Set<?> searchAcrossMultiIndices(SearchMultiIndexesRequestDto requestDto) {
        Pageable pageable = PageRequest.of(requestDto.getPage(), requestDto.getSize());
        BiFunction<String, Pageable, Set<?>> strategy = searchStrategies.get(requestDto.getSearchCategory());

        if (strategy == null) {
            throw new IllegalArgumentException("Unsupported IndexType: " + requestDto.getSearchCategory());
        }

        return strategy.apply(requestDto.getSearchTerm(), pageable);
    }

    private Map<IndexType, BiFunction<String, Pageable, Set<?>>> initSearchStrategies() {
        return Map.of(
                IndexType.POST_INDEX, (searchTerm, pageable) ->
                        postIndexRepository.fuzzyFullTextSearch(searchTerm, pageable).stream()
                                .map(postIndexMapper::mapPostIndexToPostIndexResponseDto)
                                .collect(Collectors.toSet()),

                IndexType.COMMENT_INDEX, (searchTerm, pageable) ->
                        commentIndexRepository.fuzzyFullTextSearch(searchTerm, pageable).stream()
                                .map(commentIndexMapper::mapCommentIndexToCommentIndexResponseDto)
                                .collect(Collectors.toSet()),

                IndexType.APP_USER_INDEX, (searchTerm, pageable) ->
                        appUserIndexRepository.fuzzyFullTextSearch(searchTerm, pageable).stream()
                                .map(appUserIndexMapper::mapAppUserIndexToAppUserResponseDto)
                                .collect(Collectors.toSet())
        );
    }



}

