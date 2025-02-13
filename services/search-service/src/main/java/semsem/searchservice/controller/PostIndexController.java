package semsem.searchservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import semsem.searchservice.enums.IndexType;
import semsem.searchservice.model.PostIndex;
import semsem.searchservice.service.PostIndexService;

import java.time.Instant;

@RestController
@RequestMapping("/api/v1/post-index")
@RequiredArgsConstructor
public class PostIndexController {
    private final PostIndexService postIndexService;

    @PostMapping("/fuzzy-full-text-search")
    public ResponseEntity<?> fuzzyFullTextSearch(
            @RequestParam String keyword,
            @RequestParam int size,
            @RequestParam int page
    ) {
        return ResponseEntity.ok(postIndexService.fuzzyFullTextSearch(keyword, size, page));
    }

    @PostMapping("/save")
    private void save() {
        PostIndex postIndex =  PostIndex.builder()
                        .postId("1")
                        .indexType(IndexType.POST_INDEX)
                        .content("hello this is a search microservice using elasticsearch")
                        .authorId(1L)
                        .createdAt(Instant.now())
                        .updatedAt(Instant.now())
                            .build();

    }

}
