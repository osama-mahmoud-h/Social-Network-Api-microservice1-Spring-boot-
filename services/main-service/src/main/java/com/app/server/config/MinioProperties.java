package com.app.server.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "minio")
@Data
public class MinioProperties {
    private String endpoint;
    private String accessKey;
    private String secretKey;
    private String bucketName;
    private boolean autoCreateBucket = true;
    private int urlExpiration = 604800; // 7 days in seconds
    private Folders folders = new Folders();

    @Data
    public static class Folders {
        private String profiles = "profiles/";
        private String posts = "posts/";
    }
}