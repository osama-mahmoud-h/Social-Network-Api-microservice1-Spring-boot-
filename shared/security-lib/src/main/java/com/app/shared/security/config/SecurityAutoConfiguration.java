package com.app.shared.security.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "com.app.shared.security")
@EnableFeignClients(basePackages = "com.app.shared.security.client")
public class SecurityAutoConfiguration {
}
