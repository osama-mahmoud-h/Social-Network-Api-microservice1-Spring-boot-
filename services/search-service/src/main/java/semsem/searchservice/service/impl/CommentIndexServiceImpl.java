package semsem.searchservice.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import semsem.searchservice.dto.response.CommentIndexResponseDto;
import semsem.searchservice.mapper.CommentIndexMapper;
import semsem.searchservice.model.CommentIndex;
import semsem.searchservice.repository.CommentIndexRepository;
import semsem.searchservice.service.CommentIndexService;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentIndexServiceImpl implements CommentIndexService {
    private final CommentIndexRepository commentIndexRepository;
    private final CommentIndexMapper commentIndexMapper;
    private final ElasticsearchClient elasticsearchClient;

    @Override
    public Set<CommentIndexResponseDto> fullTextSearch(String searchTerm, int size, int page) {
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        return commentIndexRepository.fuzzyFullTextSearch(searchTerm, pageable)
                .stream()
                .map(commentIndexMapper::mapCommentIndexToCommentIndexResponseDto)
                .collect(Collectors.toSet());
    }

    @Override
    public String save(CommentIndex commentIndex) {
        try {

            IndexResponse response = elasticsearchClient.index(i -> i
                    .index("comment_index")
                    .document(commentIndex)
            );
            System.out.println("Comment saved successfully: " + response);
            return response.id();
        } catch (Exception e) {
            System.out.println("Error saving comment: " + e.getMessage());
            throw new RuntimeException("Error saving comment: " + e.getMessage(), e);
        }
    }

    @Override
    public void update(CommentIndex commentIndex) {
        try {
            // Find the existing document by commentId
            List<CommentIndex> existingComments = commentIndexRepository.findByCommentId(commentIndex.getCommentId());

            if (existingComments.isEmpty()) {
                throw new RuntimeException("Comment not found with commentId: " + commentIndex.getCommentId());
            }

            // Update the document - set the Elasticsearch ID from the existing document
            commentIndex.setId(existingComments.get(0).getId());

            // Save (update) the document
            elasticsearchClient.index(i -> i
                    .index("comment_index")
                    .id(commentIndex.getId())
                    .document(commentIndex)
            );

            System.out.println("Comment updated successfully with ID: " + commentIndex.getId());
        } catch (Exception e) {
            System.out.println("Error updating comment: " + e.getMessage());
            throw new RuntimeException("Error updating comment: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteByCommentId(Long commentId) {
        try {
            // Find the document by commentId
            List<CommentIndex> existingComments = commentIndexRepository.findByCommentId(commentId);

            if (existingComments.isEmpty()) {
                System.out.println("Comment not found with commentId: " + commentId);
                return;
            }

            // Delete by Elasticsearch document ID
            elasticsearchClient.delete(d -> d
                    .index("comment_index")
                    .id(existingComments.get(0).getId())
            );

            System.out.println("Comment deleted successfully with commentId: " + commentId);
        } catch (Exception e) {
            System.out.println("Error deleting comment: " + e.getMessage());
            throw new RuntimeException("Error deleting comment: " + e.getMessage(), e);
        }
    }
}
