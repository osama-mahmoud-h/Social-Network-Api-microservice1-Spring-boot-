package com.app.shared.observability.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Getter
@Setter
@ConfigurationProperties(prefix = "observability")
public class ObservabilityProperties {

    private final Http http = new Http();
    private final Async async = new Async();

    @Getter
    @Setter
    public static class Http {
        private boolean enabled = true;
        private List<String> skipPatterns = List.of(
                "/actuator/**",
                "/api-docs/**",
                "/api-docs",
                "/v3/api-docs/**",
                "/swagger-ui/**",
                "/swagger-ui.html",
                "/favicon.ico"
        );
        private int slowRequestThresholdMs = 1000;
    }

    @Getter
    @Setter
    public static class Async {
        private boolean mdcPropagation = true;
    }
}