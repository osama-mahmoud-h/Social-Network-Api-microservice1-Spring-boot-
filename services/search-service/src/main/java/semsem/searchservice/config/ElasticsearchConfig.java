package semsem.searchservice.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URI;

@Configuration
public class ElasticsearchConfig {

    @Value("${spring.elasticsearch.uris}")
    private String elasticsearchUri;

    @Bean
    public ElasticsearchClient elasticsearchClient() {
        // Create Jackson mapper with Java Time support
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // Parse Elasticsearch URI from configuration (supports both dev and docker profiles)
        URI uri = URI.create(elasticsearchUri);
        String host = uri.getHost();
        int port = uri.getPort() > 0 ? uri.getPort() : 9200;
        String scheme = uri.getScheme() != null ? uri.getScheme() : "http";

        // Build Elasticsearch client with configured host
        RestClient restClient = RestClient.builder(
                new HttpHost(host, port, scheme)
        ).build();

        return new ElasticsearchClient(
                new RestClientTransport(restClient, new JacksonJsonpMapper(objectMapper))
        );
    }
}

