package semsem.searchservice.handler.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import semsem.searchservice.enums.EntityEventType;
import semsem.searchservice.mapper.CommentIndexMapper;
import semsem.searchservice.model.CommentIndex;
import semsem.searchservice.service.CommentIndexService;

@Component("updateCommentEventHandler")
@RequiredArgsConstructor
public class UpdateCommentEventHandler implements CommentEventHandler {
    private final CommentIndexMapper commentIndexMapper;
    private final CommentIndexService commentIndexService;

    @Override
    public EntityEventType getEventType() {
        return EntityEventType.COMMENT_UPDATED;
    }

    @Override
    public void handleEvent(Object entityEventDto) {
        CommentIndex commentIndex = commentIndexMapper.mapCommentEventObjectToCommentIndex(entityEventDto);
        commentIndexService.update(commentIndex);
        System.out.println("Comment index updated with commentId: " + commentIndex.getCommentId());
    }
}