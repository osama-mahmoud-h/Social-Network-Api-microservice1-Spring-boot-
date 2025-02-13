package semsem.searchservice.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;
import semsem.searchservice.model.CommentIndex;

import java.util.List;

@Repository
public interface CommentIndexRepository extends ElasticsearchRepository<CommentIndex, String> {

    //1. ✅ Full-text fuzzy search using the content field
    @Query("""
            {
              "match": {
                "content": {
                  "query": "?0",
                  "fuzziness": "AUTO"
                }
              }
            }
            """)
    List<CommentIndex> fuzzyFullTextSearch(String content, Pageable pageable);

    //2. ✅ find all comments by postId
    @Query("""
            {
              "match": {
                "postId": "?0"
              }
            }
            """)
    List<CommentIndex> findByPostId(String postId, Pageable pageable);

    //3. ✅ Update comment content by id
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
    void updateCommentContent(String commentId, String newContent);

    //4. ✅ Delete comment by id
    @Query("""
            {
              "query": {
                "match": {
                  "_id": "?0"
                }
              }
            }
            """)
    void deleteCommentById(String commentId);



}
