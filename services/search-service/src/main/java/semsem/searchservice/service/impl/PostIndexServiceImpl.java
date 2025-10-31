package semsem.searchservice.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.stereotype.Service;
import semsem.searchservice.dto.response.PostIndexResponseDto;
import semsem.searchservice.mapper.PostIndexMapper;
import semsem.searchservice.model.PostIndex;
import semsem.searchservice.repository.PostIndexRepository;
import semsem.searchservice.service.PostIndexService;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostIndexServiceImpl implements PostIndexService {
    private final PostIndexRepository postIndexRepository;
    private final PostIndexMapper postIndexMapper;
    private final ElasticsearchClient elasticsearchClient;

    // ✅ Full-text fuzzy search across multiple fields
    @Override
    public Set<PostIndexResponseDto> fuzzyFullTextSearch(String keyword, int size, int page) {
        Pageable pageable = Pageable.ofSize(size).withPage(page);

        return postIndexRepository.fuzzyFullTextSearch(keyword, pageable).stream()
                .map(postIndexMapper::mapPostIndexToPostIndexResponseDto)
                .collect(Collectors.toSet());
    }

    @Override
    public String save(PostIndex postIndex) {
        try {
            IndexResponse response = elasticsearchClient.index(i -> i
                    .index("post_index")
                    .document(postIndex)
            );
            System.out.println("Post saved successfully: " + response);
            // Set postId to null after saving to avoid conflicts with the original postId
            return response.id();
        } catch (Exception e) {
            System.out.println("Error saving post: " + e.getMessage());
            throw new RuntimeException("Error setting postId to null: " + e.getMessage(), e);
        }
    }

    @Override
    public List<PostIndexResponseDto> findAllPosts(int size, int page) {
    Pageable pageable = Pageable.ofSize(size).withPage(page);
        return postIndexRepository.findAllPosts(pageable).stream()
                .map(postIndexMapper::mapPostIndexToPostIndexResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public void update(PostIndex postIndex) {
        try {
            // Find the existing document by postId
            List<PostIndex> existingPosts = postIndexRepository.findByPostId(postIndex.getPostId());

            if (existingPosts.isEmpty()) {
                throw new RuntimeException("Post not found with postId: " + postIndex.getPostId());
            }

            // Update the document - set the Elasticsearch ID from the existing document
            postIndex.setId(existingPosts.get(0).getId());

            // Save (update) the document
            elasticsearchClient.index(i -> i
                    .index("post_index")
                    .id(postIndex.getId())
                    .document(postIndex)
            );

            System.out.println("Post updated successfully with ID: " + postIndex.getId());
        } catch (Exception e) {
            System.out.println("Error updating post: " + e.getMessage());
            throw new RuntimeException("Error updating post: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteByPostId(Long postId) {
        try {
            // Find the document by postId
            List<PostIndex> existingPosts = postIndexRepository.findByPostId(postId);

            if (existingPosts.isEmpty()) {
                System.out.println("Post not found with postId: " + postId);
                return;
            }

            // Delete by Elasticsearch document ID
            elasticsearchClient.delete(d -> d
                    .index("post_index")
                    .id(existingPosts.get(0).getId())
            );

            System.out.println("Post deleted successfully with postId: " + postId);
        } catch (Exception e) {
            System.out.println("Error deleting post: " + e.getMessage());
            throw new RuntimeException("Error deleting post: " + e.getMessage(), e);
        }
    }

}
