package semsem.searchservice.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import semsem.searchservice.dto.response.CommentIndexResponseDto;
import semsem.searchservice.model.CommentIndex;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CommentIndexMapper {
    private final AppUserIndexMapper appUserIndexMapper;

    public Object mapCommentToCommentIndex(Object comment) {
        return null;
    }

    public CommentIndexResponseDto mapCommentIndexToCommentIndexResponseDto(CommentIndex comment){
        return CommentIndexResponseDto.builder()
                .commentId(comment.getCommentId())
                .content(comment.getContent())
                .parentCommentId(comment.getParentCommentId())
                .postId(comment.getPostId())
                .author(appUserIndexMapper.mapAppUserIndexToAppUserResponseDto(comment.getAuthor()))
                .build();
    }

    public CommentIndex mapCommentEventObjectToCommentIndex(Object commentEvent) {
        if (commentEvent instanceof Map) {
            Map<String, Object> commentMap = (Map<String, Object>) commentEvent;
            double createAtEpoch = Double.parseDouble(commentMap.get("createdAt").toString());
            Instant createdAtInstant = Instant.ofEpochSecond((long) createAtEpoch, (long) ((createAtEpoch % 1) * 1_000_000_000));

            Instant updatedAt = Optional.ofNullable(commentMap.get("updatedAt"))
                    .map(Object::toString)
                    .flatMap(s -> {
                        try {
                            return Optional.of(Instant.ofEpochMilli((long)Double.parseDouble(s)));
                        } catch (NumberFormatException|NullPointerException e) {
                            return Optional.empty();
                        }
                    }).orElse(null);

            return CommentIndex.builder()
                    .commentId(Long.valueOf(commentMap.get("commentId").toString()))
                    .content(commentMap.get("content").toString())
                    .parentCommentId(commentMap.containsKey("parentComment") ?
                        Long.valueOf(commentMap.get("commentId").toString()) : null)
                    .postId(Long.valueOf(commentMap.get("postId").toString()))
                    .author(null) // Assuming author is handled separately
                    .authorId(commentMap.containsKey("author") ?
                        Long.valueOf(((Map<String, Object>) commentMap.get("author")).get("userId").toString()) : null)
                    .createdAt(createdAtInstant)
                    .updatedAt(updatedAt)
                    .build();
        }
        return null; // or throw an exception if the input is not as expected
    }

}
