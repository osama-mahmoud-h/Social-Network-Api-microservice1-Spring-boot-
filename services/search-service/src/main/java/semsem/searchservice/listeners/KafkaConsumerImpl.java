package semsem.searchservice.listeners;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import semsem.searchservice.handler.comment.CommentEventHandler;
import semsem.searchservice.handler.post.PostEventHandler;

import java.util.Map;


@Service
@RequiredArgsConstructor
public class KafkaConsumerImpl implements KafkaConsumer{
    private static final Logger log = LoggerFactory.getLogger(KafkaConsumerImpl.class);
    private final Map<String, PostEventHandler> postHandlerMap;
    private final Map<String, CommentEventHandler> commentHandlerMap;

    @KafkaListener(topics = "post-events", groupId = "notification-group", containerFactory = "kafkaListenerContainerFactory")
    public void listenPostEvents(ConsumerRecord<String, Object> record) {

        System.out.println("Post event received: " + record.value());
        log.info("Post event received: {}", record.value());

        Map<String,Object> eventDto = (Map<String, Object>) record.value();
        Object postObject = eventDto.get("post");
        String actionType = eventDto.containsKey("actionType") ? (String) eventDto.get("actionType").toString().toLowerCase() : null;

        if(actionType!=null && actionType.contains("create")){
            PostEventHandler postEventHandler = postHandlerMap.get("createPostEventHandler");
            if (postEventHandler == null) {
                log.error("No handler found for create post event");
                return;
            }
            postEventHandler.handleEvent(postObject);
        }else {
            log.warn("No handler found for post event with action type: {}", actionType);
            return; // Exit if no handler is found
        }
    }

    @KafkaListener(topics = "comment-events", groupId = "notification-group", containerFactory = "kafkaListenerContainerFactory")
    public void listenCommentEvents(ConsumerRecord<String, Object> record) {
        System.out.println("Comment event received: " + record.value());
        log.info("Comment event received: {}", record.value());
        Map<String,Object> eventDto = (Map<String, Object>) record.value();
        Object commentObject = eventDto.get("comment");
        String actionType = eventDto.containsKey("actionType") ? (String) eventDto.get("actionType").toString().toLowerCase() : null;

        if(actionType!=null && actionType.contains("create")){
            CommentEventHandler commentEventHandler = commentHandlerMap.get("createCommentEventHandler");
            if (commentEventHandler == null) {
                log.error("No handler found for create comment event");
                return;
            }
            commentEventHandler.handleEvent(commentObject);
        } else {
            log.warn("No handler found for comment event with action type: {}", actionType);
            return; // Exit if no handler is found
        }
    }
}
