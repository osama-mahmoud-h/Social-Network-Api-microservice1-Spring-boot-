package semsem.searchservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import semsem.searchservice.model.PostIndex;
import semsem.searchservice.service.PostIndexService;

@RestController
@RequestMapping("/api/v1/post-index")
@RequiredArgsConstructor
public class PostIndexController {
    private final PostIndexService postIndexService;

    @PostMapping("/fuzzy-full-text-search")
    public ResponseEntity<?> fuzzyFullTextSearch(
            @RequestParam String keyword
    ) {
        return ResponseEntity.ok(postIndexService.fuzzyFullTextSearch(keyword));
    }

    @PostMapping("/save")
    public void save() {
        PostIndex postIndex = new PostIndex();
        postIndex.setPostId("1");
        postIndex.setContent("hello this is a search microservice using elasticsearch");
        postIndexService.save(postIndex);

        System.out.println("okkkkkkkkkkkk");
    }

}
