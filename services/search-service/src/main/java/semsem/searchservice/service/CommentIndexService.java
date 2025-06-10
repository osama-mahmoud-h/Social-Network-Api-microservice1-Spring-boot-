package semsem.searchservice.service;

import semsem.searchservice.dto.response.CommentIndexResponseDto;
import semsem.searchservice.model.CommentIndex;

import java.util.Set;

public interface CommentIndexService {
    Set<CommentIndexResponseDto> fullTextSearch(String searchTerm, int size, int page);

    String save(CommentIndex commentIndex);
}
