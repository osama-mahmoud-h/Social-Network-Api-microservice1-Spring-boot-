package semsem.searchservice.handler.post;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import semsem.searchservice.enums.EntityEventType;
import semsem.searchservice.service.PostIndexService;

import java.util.Map;

@Component("deletePostEventHandler")
@RequiredArgsConstructor
public class DeletePostEventHandler implements PostEventHandler {
    private final PostIndexService postIndexService;

    @Override
    public EntityEventType getEventType() {
        return EntityEventType.POST_DELETED;
    }

    @Override
    public void handleEvent(Object entityEventDto) {
        if (entityEventDto instanceof Map) {
            Map<String, Object> postMap = (Map<String, Object>) entityEventDto;
            Long postId = Long.valueOf(postMap.get("postId").toString());
            postIndexService.deleteByPostId(postId);
            System.out.println("Post index deleted with postId: " + postId);
        }
    }
}