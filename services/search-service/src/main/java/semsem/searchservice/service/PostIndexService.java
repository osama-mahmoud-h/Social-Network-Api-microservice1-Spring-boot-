package semsem.searchservice.service;


import semsem.searchservice.model.PostIndex;

import java.util.List;

public interface PostIndexService {
    // ✅ Full-text fuzzy search across multiple fields
    List<PostIndex> fuzzyFullTextSearch(String keyword);

    void save(PostIndex postIndex);
}
