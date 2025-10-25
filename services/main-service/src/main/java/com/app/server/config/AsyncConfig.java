package com.app.server.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * configuration to enable asynchronous event processing.
 * allows @Async annotated methods to run in separate threads
 */
@Configuration
@EnableAsync
public class AsyncConfig {
    // spring Boot provides default async executor
    // can customize ThreadPoolTaskExecutor here if needed
}
