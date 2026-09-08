package com.portfolio.porfolio.utils.Components;

import com.portfolio.porfolio.utils.HttpStatuses;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @description Security filter that provides Rate Limiting per IP address to defend against
 *              Denial of Service (DDoS/DoS) and brute-force attacks, particularly targeting
 *              the CPU-intensive password hashing endpoint (/auth).
 */
@Component
@Order(1)
public class RateLimitingFilter extends OncePerRequestFilter {

    /** Maximum allowed requests within the time window for authentication endpoints */
    @Value("${security.rate-limit.auth.max-requests:10}")
    private int maxAuthRequests;

    /** Time window in seconds for the rate limiting counter */
    @Value("${security.rate-limit.auth.window-seconds:60}")
    private long windowSeconds;

    /** In-memory store for tracking request timestamps per client IP address */
    private final ConcurrentHashMap<String, ConcurrentLinkedQueue<Long>> requestCounts = new ConcurrentHashMap<>();

    /**
     * @description Inspects incoming requests and applies rate limiting to sensitive routes.
     *              Returns HTTP 429 (Too Many Requests) when limits are exceeded.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestUri = request.getRequestURI();

        // Apply rate limiting specifically to authentication routes (/auth)
        if (requestUri != null && requestUri.startsWith("/auth")) {
            String clientIp = extractClientIp(request);
            long currentTime = System.currentTimeMillis();
            long windowMillis = windowSeconds * 1000;

            ConcurrentLinkedQueue<Long> timestamps = requestCounts.computeIfAbsent(clientIp, k -> new ConcurrentLinkedQueue<>());

            // Remove timestamps outside the sliding time window
            synchronized (timestamps) {
                while (!timestamps.isEmpty() && (currentTime - timestamps.peek()) > windowMillis) {
                    timestamps.poll();
                }

                // Check if current request count exceeds the maximum limit
                if (timestamps.size() >= maxAuthRequests) {
                    response.setStatus(HttpStatuses.TOO_MANY_REQUESTS);
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");

                    String jsonResponse = String.format(
                            "{\"success\":false,\"code\":%d,\"message\":\"Too many requests. Please try again later.\",\"data\":null}",
                            HttpStatuses.TOO_MANY_REQUESTS
                    );
                    response.getWriter().write(jsonResponse);
                    return;
                }

                // Record the timestamp of the allowed request
                timestamps.add(currentTime);
            }
        }

        // Clean up empty/stale entries periodically to prevent memory leaks
        if (requestCounts.size() > 5000) {
            long now = System.currentTimeMillis();
            long windowMillis = windowSeconds * 1000;
            requestCounts.entrySet().removeIf(entry -> {
                ConcurrentLinkedQueue<Long> queue = entry.getValue();
                return queue.isEmpty() || (now - queue.peek()) > windowMillis;
            });
        }

        filterChain.doFilter(request, response);
    }

    /**
     * @description Extracts the real client IP address, supporting proxies and load balancers
     *              via standard X-Forwarded-For and X-Real-IP headers.
     * @param request The incoming HTTP servlet request.
     * @return The sanitized client IP address.
     */
    private String extractClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isBlank()) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isBlank()) {
            return xRealIp.trim();
        }

        return request.getRemoteAddr();
    }
}

