package semsem.searchservice.service;

import java.io.IOException;
import java.util.List;

public interface SearchService {
    List<Object> searchAcrossIndices(String searchTerm, int size, int page);

}
