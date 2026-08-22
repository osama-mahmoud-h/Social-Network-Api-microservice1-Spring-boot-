package com.app.shared.observability.filter;

import com.app.shared.observability.config.ObservabilityProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Outermost servlet filter. Owns request-scoped MDC enrichment and emits
 * one entry line and one exit line per request. All business logs inside
 * the request inherit traceId/spanId (from Micrometer Tracing) plus the
 * http.* fields pushed here, so a single Kibana query on traceId returns
 * everything that happened for that request — including the entry/exit
 * boundary lines this filter writes.
 *
 * Note: userId is populated downstream by JwtAuthenticationFilter and
 * cleared in its own finally, so it will appear on logs emitted DURING
 * the request but not on the exit line written here.
 */
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
@RequiredArgsConstructor
@Slf4j(topic = "com.app.shared.observability.http")
public class RequestLoggingFilter extends OncePerRequestFilter {

    private static final String MDC_HTTP_METHOD = "http.method";
    private static final String MDC_HTTP_URI = "http.uri";
    private static final String MDC_HTTP_REMOTE = "http.remoteIp";

    private final ObservabilityProperties properties;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        List<String> skip = properties.getHttp().getSkipPatterns();
        for (String pattern : skip) {
            if (pathMatcher.match(pattern, path)) return true;
        }
        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        String method = request.getMethod();
        String uri = request.getRequestURI();
        String query = request.getQueryString();
        String fullUri = query == null ? uri : uri + "?" + query;
        String remoteIp = resolveRemoteIp(request);

        MDC.put(MDC_HTTP_METHOD, method);
        MDC.put(MDC_HTTP_URI, uri);
        MDC.put(MDC_HTTP_REMOTE, remoteIp);

        long start = System.nanoTime();
        log.info("→ {} {}", method, fullUri);

        try {
            chain.doFilter(request, response);
        } finally {
            long durationMs = (System.nanoTime() - start) / 1_000_000L;
            int status = response.getStatus();
            boolean slow = durationMs >= properties.getHttp().getSlowRequestThresholdMs();

            if (status >= 500) {
                log.warn("← {} {} {} in {}ms", method, uri, status, durationMs);
            } else if (status >= 400 || slow) {
                log.warn("← {} {} {} in {}ms{}", method, uri, status, durationMs,
                        slow ? " (slow)" : "");
            } else {
                log.info("← {} {} {} in {}ms", method, uri, status, durationMs);
            }

            MDC.remove(MDC_HTTP_METHOD);
            MDC.remove(MDC_HTTP_URI);
            MDC.remove(MDC_HTTP_REMOTE);
        }
    }

    private String resolveRemoteIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            int comma = forwarded.indexOf(',');
            return (comma > 0 ? forwarded.substring(0, comma) : forwarded).trim();
        }
        return request.getRemoteAddr();
    }
}