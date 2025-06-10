package semsem.searchservice.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;
import semsem.searchservice.model.AppUserIndex;

import java.util.List;

@Repository
public interface AppUserIndexRepository extends ElasticsearchRepository<AppUserIndex, String> {
    //2. ✅ full-text search across multiple fields (firstName, lastName, email)
    @Query("""
            {
              "multi_match": {
                "query": "?0",
                "fields": ["firstName", "lastName", "email"]
              }
            }
            """)
    List<AppUserIndex> fuzzyFullTextSearch(String keyword, Pageable pageable);

}
