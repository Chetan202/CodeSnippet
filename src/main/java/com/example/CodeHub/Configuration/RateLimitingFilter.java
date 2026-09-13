package com.example.CodeHub.Configuration;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    private static final Set<String> LIMITED_PATHS = Set.of(
            "/login",
            "/api/auth/login",
            "/register"
    );

    private final Map<String, Window> windows = new ConcurrentHashMap<>();
    private final long windowSeconds;
    private final int maxRequests;

    public RateLimitingFilter(
            @Value("${app.security.rate-limit-window-seconds:60}") long windowSeconds,
            @Value("${app.security.rate-limit-max-requests:20}") int maxRequests) {
        this.windowSeconds = windowSeconds;
        this.maxRequests = maxRequests;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if (!shouldRateLimit(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String key = clientIp(request) + ":" + request.getRequestURI();
        long now = Instant.now().getEpochSecond();
        Window window = windows.compute(key, (ignored, current) -> {
            if (current == null || now >= current.resetAt()) {
                return new Window(1, now + windowSeconds);
            }
            return new Window(current.count() + 1, current.resetAt());
        });

        response.setHeader("X-RateLimit-Limit", String.valueOf(maxRequests));
        response.setHeader("X-RateLimit-Remaining", String.valueOf(Math.max(0, maxRequests - window.count())));
        response.setHeader("X-RateLimit-Reset", String.valueOf(window.resetAt()));

        if (window.count() > maxRequests) {
            response.setStatus(429);
            response.setContentType("text/plain");
            response.getWriter().write("Too many requests. Please wait and try again.");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean shouldRateLimit(HttpServletRequest request) {
        if (!LIMITED_PATHS.contains(request.getRequestURI())) {
            return false;
        }
        return "POST".equalsIgnoreCase(request.getMethod());
    }

    private String clientIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private record Window(int count, long resetAt) {
    }
}
