package com.app.server.service;

import com.app.server.dto.response.SearchResultsResponseDto;
import com.app.server.enums.SearchEntityType;

public interface SearchOrchestrationService {

    SearchResultsResponseDto<?> search(String searchTerm, SearchEntityType entityType, int size, int page);
}