package com.app.shared.events;

import com.app.shared.events.type.CommentActionType;
import com.app.shared.events.type.FriendshipActionType;
import com.app.shared.events.type.NotificationType;
import com.app.shared.events.type.PostActionType;
import com.app.shared.events.type.ReactionActionType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Pins the serialised shape of every integration event.
 *
 * <p>These events are a contract between separately deployed services, so the JSON itself is the
 * thing under test — not the Java types. A rename that the compiler would happily accept on both
 * sides still breaks consumers already running in production; that is exactly the failure these
 * assertions exist to catch.
 */
class EventContractTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    @DisplayName("post event serialises to the agreed field names")
    void postEventShape() throws Exception {
        PostEventDto event = PostEventDto.builder()
                .actionType(PostActionType.CREATE)
                .postId(42L)
                .post(PostEventDto.PostData.builder()
                        .postId(42L)
                        .content("hello")
                        .publicity("PUBLIC")
                        .createdAt(1_700_000_000L)
                        .updatedAt(1_700_000_500L)
                        .author(AuthorData.builder()
                                .userId(7L)
                                .firstName("Ada")
                                .lastName("Lovelace")
                                .build())
                        .build())
                .build();

        JsonNode json = mapper.valueToTree(event);

        assertThat(fieldNames(json)).containsExactlyInAnyOrder("actionType", "postId", "post");
        assertThat(json.get("actionType").asText()).isEqualTo("CREATE");
        assertThat(json.get("postId").asLong()).isEqualTo(42L);

        JsonNode post = json.get("post");
        assertThat(fieldNames(post)).containsExactlyInAnyOrder(
                "postId", "content", "publicity", "createdAt", "updatedAt", "author");
        assertThat(post.get("publicity").asText()).isEqualTo("PUBLIC");
        assertThat(post.get("createdAt").asLong()).isEqualTo(1_700_000_000L);

        JsonNode author = post.get("author");
        assertThat(fieldNames(author)).containsExactlyInAnyOrder("userId", "firstName", "lastName");
        assertThat(author.get("userId").asLong()).isEqualTo(7L);
    }

    @Test
    @DisplayName("comment event serialises to the agreed field names")
    void commentEventShape() throws Exception {
        CommentEventDto event = CommentEventDto.builder()
                .actionType(CommentActionType.REPLY)
                .commentId(9L)
                .comment(CommentEventDto.CommentData.builder()
                        .commentId(9L)
                        .content("nice one")
                        .createdAt(1_700_000_000L)
                        .updatedAt(null)
                        .postId(42L)
                        .postAuthorId(7L)
                        .parentCommentId(8L)
                        .author(AuthorData.builder().userId(3L).firstName("Grace").lastName("Hopper").build())
                        .build())
                .build();

        JsonNode json = mapper.valueToTree(event);

        assertThat(fieldNames(json)).containsExactlyInAnyOrder("actionType", "commentId", "comment");
        assertThat(json.get("actionType").asText()).isEqualTo("REPLY");

        JsonNode comment = json.get("comment");
        assertThat(fieldNames(comment)).containsExactlyInAnyOrder(
                "commentId", "content", "createdAt", "updatedAt", "postId",
                "postAuthorId", "parentCommentId", "author");
        assertThat(comment.get("postAuthorId").asLong()).isEqualTo(7L);
        assertThat(comment.get("updatedAt").isNull()).isTrue();
    }

    @Test
    @DisplayName("notification event serialises to the agreed field names")
    void notificationEventShape() {
        NotificationEvent event = NotificationEvent.builder()
                .type(NotificationType.COMMENTED_YOUR_POST)
                .message("Grace Hopper commented on your post")
                .senderId(3L)
                .receiverId(7L)
                .build();

        JsonNode json = mapper.valueToTree(event);

        assertThat(fieldNames(json)).containsExactlyInAnyOrder("type", "message", "senderId", "receiverId");
        assertThat(json.get("type").asText()).isEqualTo("COMMENTED_YOUR_POST");
    }

    @Test
    @DisplayName("reaction event serialises to the agreed field names")
    void reactionEventShape() {
        ReactionEventDto event = ReactionEventDto.builder()
                .actionType(ReactionActionType.ADDED)
                .reactionType("LIKE")
                .targetType("POST")
                .targetId(42L)
                .postId(42L)
                .reactorUserId(3L)
                .build();

        JsonNode json = mapper.valueToTree(event);

        assertThat(fieldNames(json)).containsExactlyInAnyOrder(
                "actionType", "reactionType", "targetType", "targetId", "postId", "reactorUserId");
        assertThat(json.get("actionType").asText()).isEqualTo("ADDED");
        assertThat(json.get("reactionType").asText()).isEqualTo("LIKE");
    }

    @Test
    @DisplayName("friendship event serialises to the agreed field names")
    void friendshipEventShape() {
        FriendshipEventDto event = FriendshipEventDto.builder()
                .actionType(FriendshipActionType.ACCEPTED)
                .userId1(3L)
                .userId2(7L)
                .build();

        JsonNode json = mapper.valueToTree(event);

        assertThat(fieldNames(json)).containsExactlyInAnyOrder("actionType", "userId1", "userId2");
        assertThat(json.get("actionType").asText()).isEqualTo("ACCEPTED");
    }

    @Test
    @DisplayName("getEventType() is routing metadata and must never reach the wire")
    void eventTypeIsNotSerialised() {
        assertThat(fieldNames(mapper.valueToTree(NotificationEvent.builder().build()))).doesNotContain("eventType");
        assertThat(fieldNames(mapper.valueToTree(PostEventDto.builder().build()))).doesNotContain("eventType");
        assertThat(fieldNames(mapper.valueToTree(CommentEventDto.builder().build()))).doesNotContain("eventType");
        assertThat(fieldNames(mapper.valueToTree(ReactionEventDto.builder().build()))).doesNotContain("eventType");
        assertThat(fieldNames(mapper.valueToTree(FriendshipEventDto.builder().build()))).doesNotContain("eventType");
    }

    @Test
    @DisplayName("a consumer on an older build tolerates fields a newer producer added")
    void unknownFieldsAreIgnored() throws Exception {
        String futureProducerPayload = """
                {
                  "actionType": "CREATE",
                  "postId": 42,
                  "somethingAddedLater": "surprise",
                  "post": {
                    "postId": 42,
                    "content": "hello",
                    "alsoNew": true,
                    "author": {"userId": 7, "firstName": "Ada", "lastName": "Lovelace", "nickname": "A"}
                  }
                }
                """;

        PostEventDto event = mapper.readValue(futureProducerPayload, PostEventDto.class);

        assertThat(event.getActionType()).isEqualTo(PostActionType.CREATE);
        assertThat(event.getPostId()).isEqualTo(42L);
        assertThat(event.getPost().getAuthor().getFirstName()).isEqualTo("Ada");
    }

    @Test
    @DisplayName("every event survives a producer -> consumer round trip")
    void roundTrip() throws Exception {
        CommentEventDto original = CommentEventDto.builder()
                .actionType(CommentActionType.CREATE)
                .commentId(9L)
                .comment(CommentEventDto.CommentData.builder()
                        .commentId(9L)
                        .content("round trip")
                        .postId(42L)
                        .postAuthorId(7L)
                        .author(AuthorData.builder().userId(3L).firstName("Grace").lastName("Hopper").build())
                        .build())
                .build();

        CommentEventDto restored = mapper.readValue(mapper.writeValueAsString(original), CommentEventDto.class);

        assertThat(restored).isEqualTo(original);
    }

    private static Iterable<String> fieldNames(JsonNode node) {
        return node::fieldNames;
    }
}