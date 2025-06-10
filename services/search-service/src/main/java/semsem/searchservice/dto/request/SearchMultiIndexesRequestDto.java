package semsem.searchservice.dto.request;

import lombok.Data;
import semsem.searchservice.enums.IndexType;

@Data
public class SearchMultiIndexesRequestDto {
    private String searchTerm;
    private IndexType searchCategory;
    private int size;
    private int page;
}
