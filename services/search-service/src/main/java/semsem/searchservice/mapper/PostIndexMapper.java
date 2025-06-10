package semsem.searchservice.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import semsem.searchservice.dto.response.PostIndexResponseDto;
import semsem.searchservice.model.AppUserIndex;
import semsem.searchservice.model.PostIndex;

import javax.annotation.Nullable;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PostIndexMapper {

    private final AppUserIndexMapper appUserIndexMapper;

    public PostIndex mapPostEventObjectToPostIndex(Object post) {
        if (post instanceof Map) {
            Map<String, Object> postMap = (Map<String, Object>) post;

            double createAtEpoch = Double.parseDouble(postMap.get("createdAt").toString());
            Instant createdAtInstant = Instant.ofEpochSecond((long) createAtEpoch, (long) ((createAtEpoch % 1) * 1_000_000_000));

            Instant updatedAtInstant = Optional.ofNullable(postMap.get("updatedAt"))
                    .map(Object::toString)
                    .flatMap(s -> {
                        try {
                            return Optional.of(Instant.ofEpochMilli((long)Double.parseDouble(s)));
                        } catch (NumberFormatException|NullPointerException e) {
                            return Optional.empty();
                        }
                    }).orElse(null);

            return PostIndex.builder()
                    .postId(Long.valueOf(postMap.get("postId").toString()))
                    .content(postMap.get("content").toString())
                    .createdAt(createdAtInstant)
                    .updatedAt(updatedAtInstant)
                    .authorId(postMap.containsKey("author") ?
                        Long.valueOf(((Map<String, Object>) postMap.get("author")).get("userId").toString()) : null)
                    .author(null)
                    .build();

        }
        return null; // or throw an exception if the input is not as expected
    }

    public PostIndexResponseDto mapPostIndexToPostIndexResponseDto(PostIndex post){
        return PostIndexResponseDto.builder()
                .postId(post.getPostId())
                .content(post.getContent())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .author(Optional.ofNullable(post.getAuthor()).map(appUserIndexMapper::mapAppUserIndexToAppUserResponseDto).orElse(null))
                .build();
    }

}
