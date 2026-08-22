package semsem.chatservice;

import com.datastax.oss.driver.api.core.CqlSession;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import semsem.chatservice.repository.ConversationMessageRepository;

/**
 * Smoke test: the whole application context wires up.
 * <p>
 * {@code CassandraConfig} builds its session with RECONNECT_ON_INIT, so with no
 * node listening the real bean retries forever and the test hangs rather than
 * fails. The session and the one Cassandra-backed repository are mocked so this
 * stays a wiring check that needs no infrastructure — exercising the actual
 * queries needs Testcontainers and belongs in a separate integration test.
 * Redis and Kafka connect lazily, so they need no stand-in here.
 */
@SpringBootTest
@ActiveProfiles("test")
class ChatServiceApplicationTests {

    @MockitoBean
    private CqlSession cqlSession;

    @MockitoBean
    private ConversationMessageRepository conversationMessageRepository;

    @Test
    void contextLoads() {
    }

}