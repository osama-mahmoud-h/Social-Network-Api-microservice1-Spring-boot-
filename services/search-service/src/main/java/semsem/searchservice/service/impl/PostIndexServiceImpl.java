package semsem.searchservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import semsem.searchservice.model.PostIndex;
import semsem.searchservice.repository.PostIndexRepository;
import semsem.searchservice.service.PostIndexService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostIndexServiceImpl implements PostIndexService {
    private final PostIndexRepository postIndexRepository;

    // ✅ Full-text fuzzy search across multiple fields
    @Override
    public List<PostIndex> fuzzyFullTextSearch(String keyword) {
        List<PostIndex> posts =  postIndexRepository.fuzzyFullTextSearch(keyword);

        System.out.println("searched posts :"+ posts);
        return posts;
    }

    @Override
    public void save(PostIndex postIndex) {
        postIndexRepository.save(postIndex);
    }

}
