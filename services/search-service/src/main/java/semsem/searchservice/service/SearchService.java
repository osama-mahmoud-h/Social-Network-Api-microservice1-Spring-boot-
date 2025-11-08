package semsem.searchservice.service;

import semsem.searchservice.dto.request.SearchMultiIndexesRequestDto;
import semsem.searchservice.dto.response.SearchIdsResponseDto;

import java.util.Set;

public interface SearchService {

    Set<?> searchAcrossMultiIndices(SearchMultiIndexesRequestDto searchMultiIndexesRequestDto);

    SearchIdsResponseDto searchAndReturnIdsOnly(SearchMultiIndexesRequestDto searchMultiIndexesRequestDto);
}
