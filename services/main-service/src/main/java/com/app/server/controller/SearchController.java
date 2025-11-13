package com.app.server.controller;

import com.app.server.dto.response.SearchResultsResponseDto;
import com.app.server.enums.SearchEntityType;
import com.app.server.service.SearchOrchestrationService;
import com.app.shared.security.dto.MyApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchOrchestrationService searchOrchestrationService;

    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<MyApiResponse<SearchResultsResponseDto<?>>> search(
            @RequestParam String query,
            @RequestParam SearchEntityType type,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "0") int page
    ) {
        SearchResultsResponseDto<?> results = searchOrchestrationService.search(query, type, size, page);
        return ResponseEntity.ok(
                MyApiResponse.success("Search completed successfully", results)
        );
    }
}
