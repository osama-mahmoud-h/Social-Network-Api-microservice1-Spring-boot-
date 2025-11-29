package semsem.apigetway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
            .csrf(ServerHttpSecurity.CsrfSpec::disable)  // Disable CSRF
            .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)  // Disable HTTP Basic
            .formLogin(ServerHttpSecurity.FormLoginSpec::disable)  // Disable form login
            .authorizeExchange(exchange -> exchange
                .anyExchange().permitAll()  // Allow all requests (no authentication)
            );

        return http.build();
    }
}