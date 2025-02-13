package semsem.searchservice.service;


import semsem.searchservice.dto.response.PostIndexResponseDto;
import semsem.searchservice.model.PostIndex;

import java.util.List;
import java.util.Set;

public interface PostIndexService {

    // ✅ Full-text fuzzy search across multiple fields
    Set<PostIndexResponseDto> fuzzyFullTextSearch(String keyword, int size, int page);

    void save(PostIndex postIndex);
}
