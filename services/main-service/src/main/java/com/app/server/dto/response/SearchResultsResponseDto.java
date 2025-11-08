package com.app.server.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchResultsResponseDto<T> {
    private String entityType;
    private List<T> results;
    private int totalResults;
    private int page;
    private int size;
}