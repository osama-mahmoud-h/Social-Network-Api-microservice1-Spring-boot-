package semsem.searchservice.handler.post;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import semsem.searchservice.dto.event.EntityEventDto;
import semsem.searchservice.enums.EntityEventType;
import semsem.searchservice.mapper.PostIndexMapper;
import semsem.searchservice.model.PostIndex;
import semsem.searchservice.service.PostIndexService;

@Component("createPostEventHandler")
@RequiredArgsConstructor
public class CreatePostEventHandler implements PostEventHandler {
    private final PostIndexMapper postIndexMapper;
    private final PostIndexService postIndexService;

    @Override
    public EntityEventType getEventType() {
        return EntityEventType.POST_CREATED;
    }

    @Override
    public void handleEvent(Object entityEventDto) {
        PostIndex postIndex = postIndexMapper.mapPostEventObjectToPostIndex(entityEventDto);
        String createdIndexId = postIndexService.save(postIndex);
        System.out.println("Post index created with ID: " + createdIndexId);
    }
}
