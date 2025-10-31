package semsem.searchservice.handler.post;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import semsem.searchservice.enums.EntityEventType;
import semsem.searchservice.mapper.PostIndexMapper;
import semsem.searchservice.model.PostIndex;
import semsem.searchservice.service.PostIndexService;

@Component("updatePostEventHandler")
@RequiredArgsConstructor
public class UpdatePostEventHandler implements PostEventHandler {
    private final PostIndexMapper postIndexMapper;
    private final PostIndexService postIndexService;

    @Override
    public EntityEventType getEventType() {
        return EntityEventType.POST_UPDATED;
    }

    @Override
    public void handleEvent(Object entityEventDto) {
        PostIndex postIndex = postIndexMapper.mapPostEventObjectToPostIndex(entityEventDto);
        postIndexService.update(postIndex);
        System.out.println("Post index updated with postId: " + postIndex.getPostId());
    }
}