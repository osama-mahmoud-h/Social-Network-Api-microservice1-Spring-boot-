package semsem.searchservice.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import semsem.searchservice.dto.response.PostIndexResponseDto;
import semsem.searchservice.model.PostIndex;

import java.time.Instant;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class PostIndexMapper {

    private final AppUserIndexMapper appUserIndexMapper;

    public PostIndex mapPostToPostIndex(Object post) {
       return null;
    }

    public PostIndexResponseDto mapDbObjectIndexToPostResponseDto(Map<String,Object> hitMap) {
        return PostIndexResponseDto.builder()
                .postId(Long.valueOf(hitMap.get("postId").toString()))
                .content(hitMap.get("content").toString())
                .createdAt((Instant) hitMap.get("createdAt"))
                .updatedAt((Instant) hitMap.get("updatedAt"))
               // .author(appUserIndexMapper.mapDbObjectIndexToAppUserResponseDto((Map<String, Object>) hitMap.get("author")))
                .build();
    }

    public PostIndexResponseDto mapPostIndexToPostIndexResponseDto(PostIndex post){
        return PostIndexResponseDto.builder()
                .postId(Long.valueOf(post.getPostId()))
                .content(post.getContent())
                .author(appUserIndexMapper.mapAppUserIndexToAppUserResponseDto(post.getAuthor()))
                .build();
    }

}
