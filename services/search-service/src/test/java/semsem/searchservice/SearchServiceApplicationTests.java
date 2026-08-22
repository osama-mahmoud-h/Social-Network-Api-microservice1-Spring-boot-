package semsem.searchservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import semsem.searchservice.repository.AppUserIndexRepository;
import semsem.searchservice.repository.CommentIndexRepository;
import semsem.searchservice.repository.PostIndexRepository;

/**
 * Smoke test: the whole application context wires up.
 * <p>
 * Spring Data Elasticsearch contacts the cluster while building each repository
 * proxy, so without a live node the context cannot start. The repositories are
 * mocked here so this stays a wiring check that needs no infrastructure —
 * exercising the actual queries needs Testcontainers and belongs in a separate
 * integration test.
 */
@SpringBootTest
class SearchServiceApplicationTests {

    @MockitoBean
    private PostIndexRepository postIndexRepository;

    @MockitoBean
    private CommentIndexRepository commentIndexRepository;

    @MockitoBean
    private AppUserIndexRepository appUserIndexRepository;

    @Test
    void contextLoads() {
    }

}