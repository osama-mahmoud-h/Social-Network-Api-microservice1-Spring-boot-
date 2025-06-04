package semsem.searchservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import semsem.searchservice.enums.IndexType;
import semsem.searchservice.model.CommentIndex;
import semsem.searchservice.model.PostIndex;
import semsem.searchservice.repository.CommentIndexRepository;
import semsem.searchservice.service.CommentIndexService;
import semsem.searchservice.service.PostIndexService;

import java.time.Instant;
import java.util.Random;

@RestController
@RequestMapping("/api/v1/post-index")
@RequiredArgsConstructor
public class PostIndexController {
    private final PostIndexService postIndexService;
    private final CommentIndexService commentIndexService;

    @PostMapping("/fuzzy-full-text-search")
    public ResponseEntity<?> fuzzyFullTextSearch(
            @RequestParam String keyword,
            @RequestParam int size,
            @RequestParam int page
    ) {
        return ResponseEntity.ok(postIndexService.fuzzyFullTextSearch(keyword, size, page));
    }

    @GetMapping("/find-all")
    public ResponseEntity<?> findAllPosts(
            @RequestParam int size,
            @RequestParam int page
    ) {
        return ResponseEntity.ok(postIndexService.findAllPosts(size, page));
    }

    @PostMapping("/save")
    private  ResponseEntity<?> save() {
        try {
            PostIndex postIndex = PostIndex.builder()
                    .postId(10L)
                    .indexType(IndexType.POST_INDEX)
                    .content("hello this is a search microservice using elasticsearch")
                    .authorId(1L)
                    .createdAt(Instant.now())
                    .updatedAt(Instant.now())
                    .author(null)
                    .build();

            String postId = postIndexService.save(postIndex);
            return ResponseEntity.ok("Post saved with ID: " + postId);
        }catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error saving post: " + e.getMessage());
        }

    }

}
