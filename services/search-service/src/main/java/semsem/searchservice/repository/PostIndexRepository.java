package semsem.searchservice.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import semsem.searchservice.model.PostIndex;

import java.util.List;

@Repository
public interface PostIndexRepository extends ElasticsearchRepository<PostIndex, String> {
    // ✅ Full-text fuzzy search across multiple fields
    @Query(value = """
            {
              "multi_match": {
                "query": "?0",
                "fields": ["content", "title"],
                "fuzziness": "AUTO"
              }
            }
         """)
    List<PostIndex> fuzzyFullTextSearch(String keyword, Pageable pageable);

    @Query("""
            {
              "match_all": {}
            }
            """)
    List<PostIndex> findAllPosts(Pageable pageable);

    // ✅ Update post content by id
    @Query("""
            {
              "script": {
                "source": "ctx._source.content = ?1",
                "lang": "painless"
              },
              "query": {
                "match": {
                  "_id": "?0"
                }
              }
            }
            """)
    void updatePostContent(String postId, String newContent);

    // ✅ Delete post by id
    @Query("""
            {
              "query": {
                "match": {
                  "_id": "?0"
                }
              }
            }
            """)
    void deletePostById(String postId);

    // ✅ Find post by postId (business entity ID)
    @Query("""
            {
              "match": {
                "postId": "?0"
              }
            }
            """)
    List<PostIndex> findByPostId(Long postId);


}
