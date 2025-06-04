package semsem.searchservice.handler.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import semsem.searchservice.enums.EntityEventType;
import semsem.searchservice.mapper.CommentIndexMapper;
import semsem.searchservice.model.CommentIndex;
import semsem.searchservice.service.CommentIndexService;

@Component("createCommentEventHandler")
@RequiredArgsConstructor
public class CreateCommentEventHandler implements CommentEventHandler {
    private final CommentIndexMapper commentIndexMapper;
    private final CommentIndexService commentIndexService;

    @Override
    public EntityEventType getEventType() {
        return EntityEventType.COMMENT_CREATED;
    }

    @Override
    public void handleEvent(Object entityEventDto) {
        CommentIndex commentIndex = commentIndexMapper.mapCommentEventObjectToCommentIndex(entityEventDto);
        String createdIndexId = commentIndexService.save(commentIndex);
        System.out.println("Comment index created with ID: " + createdIndexId);
    }
}
