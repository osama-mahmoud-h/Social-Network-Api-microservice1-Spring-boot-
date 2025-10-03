package semsem.apigetway.filter;

import com.app.shared.security.client.AuthServiceClient;
import com.app.shared.security.dto.TokenValidationResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class AuthenticationGatewayFilterFactory extends AbstractGatewayFilterFactory<AuthenticationGatewayFilterFactory.Config> {

    private final AuthServiceClient authServiceClient;

    public AuthenticationGatewayFilterFactory(AuthServiceClient authServiceClient) {
        super(Config.class);
        this.authServiceClient = authServiceClient;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            // Skip authentication for auth service endpoints
            if (isAuthServiceEndpoint(request.getPath().toString())) {
                return chain.filter(exchange);
            }

            String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return onError(exchange, "Missing or invalid Authorization header", HttpStatus.UNAUTHORIZED);
            }

            try {
                TokenValidationResponse validationResponse = authServiceClient.validateToken(authHeader);

                if (!validationResponse.isValid()) {
                    return onError(exchange, validationResponse.getMessage(), HttpStatus.UNAUTHORIZED);
                }

                // Add user information to request headers for downstream services
                ServerHttpRequest modifiedRequest = request.mutate()
                        .header("X-User-Id", validationResponse.getUserId().toString())
                        .header("X-User-Email", validationResponse.getEmail())
                        .header("X-User-Roles", String.join(",", validationResponse.getRoles()))
                        .build();

                return chain.filter(exchange.mutate().request(modifiedRequest).build());

            } catch (Exception e) {
                log.error("Error validating token: {}", e.getMessage());
                return onError(exchange, "Authentication failed", HttpStatus.UNAUTHORIZED);
            }
        };
    }

    private boolean isAuthServiceEndpoint(String path) {
        return path.startsWith("/api/auth/") ||
               path.startsWith("/actuator/") ||
               path.startsWith("/swagger-ui/") ||
               path.startsWith("/v3/api-docs/");
    }

    private Mono<Void> onError(ServerWebExchange exchange, String error, HttpStatus status) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().add("Content-Type", "application/json");

        String errorMessage = String.format("{\"error\":\"%s\",\"status\":%d}", error, status.value());

        return response.writeWith(Mono.just(response.bufferFactory().wrap(errorMessage.getBytes())));
    }

    public static class Config {
        // Configuration properties if needed
    }
}
