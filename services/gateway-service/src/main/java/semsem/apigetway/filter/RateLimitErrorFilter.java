package semsem.apigetway.filter;

import com.app.shared.security.dto.MyApiResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class RateLimitErrorFilter implements GlobalFilter, Ordered {

    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(exchange.getResponse()) {
            @Override
            public Mono<Void> setComplete() {
                if (HttpStatus.TOO_MANY_REQUESTS.equals(getStatusCode())) {
                    return writeRateLimitErrorBody();
                }
                return super.setComplete();
            }

            private Mono<Void> writeRateLimitErrorBody() {
                MyApiResponse<Void> body = MyApiResponse.error(
                        "Too many requests. Please slow down.",
                        "RATE_LIMIT_EXCEEDED",
                        "You have exceeded the allowed request rate. Please wait before retrying."
                );
                try {
                    byte[] bytes = objectMapper.writeValueAsBytes(body);
                    getHeaders().setContentType(MediaType.APPLICATION_JSON);
                    getHeaders().setContentLength(bytes.length);
                    DataBuffer buffer = getDelegate().bufferFactory().wrap(bytes);
                    return getDelegate().writeWith(Mono.just(buffer));
                } catch (JsonProcessingException e) {
                    log.error("Failed to serialize rate limit error response", e);
                    return super.setComplete();
                }
            }
        };

        return chain.filter(exchange.mutate().response(decoratedResponse).build());
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}