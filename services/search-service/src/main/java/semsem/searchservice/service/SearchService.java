package semsem.searchservice.service;

import semsem.searchservice.dto.request.SearchMultiIndexesRequestDto;

import java.util.Set;

public interface SearchService {

    Set<?> searchAcrossMultiIndices(SearchMultiIndexesRequestDto searchMultiIndexesRequestDto) ;
}
