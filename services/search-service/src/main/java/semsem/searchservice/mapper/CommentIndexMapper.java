package semsem.searchservice.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import semsem.searchservice.dto.response.CommentIndexResponseDto;
import semsem.searchservice.model.CommentIndex;

import java.time.Instant;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class CommentIndexMapper {
    private final AppUserIndexMapper appUserIndexMapper;

    public Object mapCommentToCommentIndex(Object comment) {
        return null;
    }

    public Object mapDbObjectIndexToCommentResponseDto(Map<String,Object> hitMap) {
        return CommentIndexResponseDto.builder()
                .commentId(Long.valueOf(hitMap.get("commentId").toString()))
                .content(hitMap.get("content").toString())
                .createdAt((Instant) hitMap.get("createdAt"))
                .updatedAt((Instant) hitMap.get("updatedAt"))
                .author(appUserIndexMapper.mapDbObjectIndexToAppUserResponseDto((Map<String, Object>) hitMap.get("author")))
                .build();
    }

    public CommentIndexResponseDto mapCommentIndexToCommentIndexResponseDto(CommentIndex comment){
        return CommentIndexResponseDto.builder()
                .commentId(Long .valueOf(comment.getCommentId()))
                .content(comment.getContent())
                .parentCommentId(comment.getParentCommentId())
                .postId(comment.getPostId())
                .author(appUserIndexMapper.mapAppUserIndexToAppUserResponseDto(comment.getAuthor()))
                .build();
    }

}
