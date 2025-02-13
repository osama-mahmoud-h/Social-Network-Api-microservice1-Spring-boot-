package semsem.searchservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import semsem.searchservice.service.SearchService;

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
    public ResponseEntity<?> search(
            @RequestParam String searchTerm,
            @RequestParam int size,
            @RequestParam int page
    ) {
        return ResponseEntity.ok(searchService.searchAcrossIndices(searchTerm, size, page));
    }

//    // ✅ Get suggestions for search input
//    @GetMapping("/suggestions")
//    public ResponseEntity<?> getSuggestions(
//            @RequestParam String inputText
//    ) {
//        return ResponseEntity.ok(searchService.getSuggestions(inputText));
//    }
}
