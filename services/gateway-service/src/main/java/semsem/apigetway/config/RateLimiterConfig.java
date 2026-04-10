package semsem.apigetway.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import reactor.core.publisher.Mono;

@Configuration
@EnableConfigurationProperties(RateLimiterProperties.class)
public class RateLimiterConfig {

    // Authenticated routes: key by user ID, fallback to IP
    @Bean
    @Primary
    public KeyResolver userKeyResolver() {
        return exchange -> Mono.justOrEmpty(
                exchange.getRequest().getHeaders().getFirst("X-User-Id")
        ).switchIfEmpty(Mono.just(
                exchange.getRequest().getRemoteAddress() != null
                        ? exchange.getRequest().getRemoteAddress().getAddress().getHostAddress()
                        : "unknown"
        ));
    }

    // Unauthenticated routes (login, register): always key by IP
    @Bean
    public KeyResolver ipKeyResolver() {
        return exchange -> Mono.just(
                exchange.getRequest().getRemoteAddress() != null
                        ? exchange.getRequest().getRemoteAddress().getAddress().getHostAddress()
                        : "unknown"
        );
    }

    @Bean
    public RedisRateLimiter authRateLimiter(RateLimiterProperties props) {
        RateLimiterProperties.RouteLimit c = props.getAuth();
        return new RedisRateLimiter(c.replenishRate(), c.burstCapacity(), c.requestedTokens());
    }

    @Bean
    @Primary
    public RedisRateLimiter defaultRateLimiter(RateLimiterProperties props) {
        RateLimiterProperties.RouteLimit c = props.getGeneral();
        return new RedisRateLimiter(c.replenishRate(), c.burstCapacity(), c.requestedTokens());
    }

    @Bean
    public RedisRateLimiter searchRateLimiter(RateLimiterProperties props) {
        RateLimiterProperties.RouteLimit c = props.getSearch();
        return new RedisRateLimiter(c.replenishRate(), c.burstCapacity(), c.requestedTokens());
    }

    @Bean
    public RedisRateLimiter chatRateLimiter(RateLimiterProperties props) {
        RateLimiterProperties.RouteLimit c = props.getChat();
        return new RedisRateLimiter(c.replenishRate(), c.burstCapacity(), c.requestedTokens());
    }
}