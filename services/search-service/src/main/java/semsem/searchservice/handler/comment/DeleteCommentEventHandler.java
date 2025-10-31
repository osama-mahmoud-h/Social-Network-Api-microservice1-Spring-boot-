package semsem.searchservice.handler.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import semsem.searchservice.enums.EntityEventType;
import semsem.searchservice.service.CommentIndexService;

import java.util.Map;

@Component("deleteCommentEventHandler")
@RequiredArgsConstructor
public class DeleteCommentEventHandler implements CommentEventHandler {
    private final CommentIndexService commentIndexService;

    @Override
    public EntityEventType getEventType() {
        return EntityEventType.COMMENT_DELETED;
    }

    @Override
    public void handleEvent(Object entityEventDto) {
        if (entityEventDto instanceof Map) {
            Map<String, Object> commentMap = (Map<String, Object>) entityEventDto;
            Long commentId = Long.valueOf(commentMap.get("commentId").toString());
            commentIndexService.deleteByCommentId(commentId);
            System.out.println("Comment index deleted with commentId: " + commentId);
        }
    }
}