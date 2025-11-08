package semsem.searchservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import semsem.searchservice.dto.request.SearchMultiIndexesRequestDto;
import semsem.searchservice.dto.response.SearchIdsResponseDto;
import semsem.searchservice.enums.IndexType;
import semsem.searchservice.service.SearchService;

import java.util.Set;

@RestController
@RequestMapping("/api/v1/search")
@RequiredArgsConstructor
public class SearchController {
    private final SearchService searchService;

    // ✅ Search across all indices
    // ✅ Pagination
    // ✅ Return total number of matching documents
    // ✅ Return search results
    // ✅ Return search results with highlighting
    @GetMapping("/general")
    public ResponseEntity<Set<?>> search(
            @ModelAttribute SearchMultiIndexesRequestDto searchMultiIndexesRequestDto
            ) {
        return ResponseEntity.ok(searchService.searchAcrossMultiIndices(searchMultiIndexesRequestDto));
    }

    // Return only IDs for main-service integration
    @GetMapping("/ids")
    public ResponseEntity<SearchIdsResponseDto> searchIds(
            @RequestParam String searchTerm,
            @RequestParam IndexType searchCategory,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "0") int page
    ) {
        SearchMultiIndexesRequestDto requestDto = new SearchMultiIndexesRequestDto();
        requestDto.setSearchTerm(searchTerm);
        requestDto.setSearchCategory(searchCategory);
        requestDto.setSize(size);
        requestDto.setPage(page);

        return ResponseEntity.ok(searchService.searchAndReturnIdsOnly(requestDto));
    }
}
