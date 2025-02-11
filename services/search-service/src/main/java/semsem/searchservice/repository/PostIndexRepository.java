package semsem.searchservice.repository;

import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import semsem.searchservice.model.PostIndex;

import java.util.List;

@Repository
public interface PostIndexRepository extends ElasticsearchRepository<PostIndex, String> {
    // ✅ Full-text fuzzy search across multiple fields
  //  @Query("{\"multi_match\": {\"query\": \"?0\", \"fields\": [\"content\", \"title\"], \"fuzziness\": \"AUTO\"}}")
    @Query("""
            {
              "multi_match": {
                "query": "?0",
                "fields": ["content", "title"],
                "fuzziness": "AUTO"
              }
            }
         """)
    List<PostIndex> fuzzyFullTextSearch(String keyword);

    @Query("""
            {
              "match_all": {}
            }
            """)
    List<PostIndex> findAllPosts();
}
