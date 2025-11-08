package com.app.server.client;

import com.app.server.dto.response.SearchIdsResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "search-service",
        url = "${search-service.url:http://localhost:8084}"
)
public interface SearchServiceClient {

    @GetMapping("/api/v1/search/ids")
    SearchIdsResponseDto searchIds(
            @RequestParam("searchTerm") String searchTerm,
            @RequestParam("searchCategory") String searchCategory,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "page", defaultValue = "0") int page
    );
}