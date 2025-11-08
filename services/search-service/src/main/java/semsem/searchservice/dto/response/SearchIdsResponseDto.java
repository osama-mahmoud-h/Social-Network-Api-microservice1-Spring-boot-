package semsem.searchservice.dto.response;

import lombok.Builder;
import lombok.Data;
import semsem.searchservice.enums.IndexType;

import java.util.List;

@Builder
@Data
public class SearchIdsResponseDto {
    private IndexType indexType;
    private List<Long> ids;
    private int totalResults;
    private int page;
    private int size;
}
