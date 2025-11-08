package com.app.server.service;

import com.app.server.dto.response.SearchResultsResponseDto;

public interface SearchOrchestrationService {

    SearchResultsResponseDto<?> search(String searchTerm, String entityType, int size, int page);
}