package semsem.chatservice.config;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.CqlSessionBuilder;
import com.datastax.oss.driver.api.core.InvalidKeyspaceException;
import com.datastax.oss.driver.api.core.config.DefaultDriverOption;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.cassandra.DriverConfigLoaderBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class CassandraConfig {

    @Bean
    public DriverConfigLoaderBuilderCustomizer reconnectOnInit() {
        return builder -> builder.withBoolean(DefaultDriverOption.RECONNECT_ON_INIT, true);
    }

    @Bean
    public CqlSession cassandraSession(CqlSessionBuilder cqlSessionBuilder) {
        try {
            return cqlSessionBuilder.build();
        } catch (InvalidKeyspaceException e) {
            log.warn("Keyspace not found. Starting without it — run cassandra/schema.cql to initialize.");
            return cqlSessionBuilder.withKeyspace((CqlIdentifier) null).build();
        }
    }
}