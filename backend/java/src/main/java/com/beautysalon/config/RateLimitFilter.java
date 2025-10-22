package com.beautysalon.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Rate Limiting Filter
 * Implements simple rate limiting based on IP address
 * Only enabled when app.rate-limit.enabled=true
 */
@Component
@Order(1)
@ConditionalOnProperty(
    prefix = "app.rate-limit",
    name = "enabled",
    havingValue = "true",
    matchIfMissing = false
)
public class RateLimitFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(RateLimitFilter.class);
    
    @Autowired
    private RateLimitProperties rateLimitProperties;
    
    // Thread-safe storage for rate limit data
    private final Map<String, RateLimitData> rateLimitMap = new ConcurrentHashMap<>();
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        logger.info("✅ RateLimitFilter ENABLED - Max requests: {}/min", 
            rateLimitProperties.getMaxRequestsPerMinute());
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        // Skip rate limiting for health check endpoints
        String path = httpRequest.getRequestURI();
        if (path.startsWith("/health") || path.startsWith("/actuator")) {
            chain.doFilter(request, response);
            return;
        }
        
        String clientIp = getClientIp(httpRequest);
        
        if (!isAllowed(clientIp)) {
            logger.warn("Rate limit exceeded for IP: {}", clientIp);
            httpResponse.setStatus(429); // Too Many Requests
            httpResponse.setHeader("X-RateLimit-Limit", String.valueOf(rateLimitProperties.getMaxRequestsPerMinute()));
            httpResponse.setHeader("X-RateLimit-Remaining", "0");
            httpResponse.setHeader("Retry-After", "60");
            httpResponse.getWriter().write("{\"error\":\"Too many requests. Please try again later.\"}");
            return;
        }
        
        // Add rate limit headers
        RateLimitData data = rateLimitMap.get(clientIp);
        httpResponse.setHeader("X-RateLimit-Limit", String.valueOf(rateLimitProperties.getMaxRequestsPerMinute()));
        httpResponse.setHeader("X-RateLimit-Remaining", 
            String.valueOf(rateLimitProperties.getMaxRequestsPerMinute() - data.getCount()));
        
        chain.doFilter(request, response);
    }
    
    /**
     * Check if request is allowed based on rate limit
     */
    private boolean isAllowed(String clientIp) {
        long currentTime = System.currentTimeMillis();
        
        rateLimitMap.putIfAbsent(clientIp, new RateLimitData());
        RateLimitData data = rateLimitMap.get(clientIp);
        
        // Clean up old entries periodically
        cleanupOldEntries(currentTime);
        
        return data.tryAcquire(currentTime, rateLimitProperties.getMaxRequestsPerMinute(), rateLimitProperties.getTimeWindowMs());
    }
    
    /**
     * Extract client IP from request
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
    
    /**
     * Clean up old rate limit entries
     */
    private void cleanupOldEntries(long currentTime) {
        rateLimitMap.entrySet().removeIf(entry -> 
            currentTime - entry.getValue().getWindowStart() > rateLimitProperties.getTimeWindowMs() * 2
        );
    }
    
    /**
     * Rate limit data for each IP
     */
    private static class RateLimitData {
        private final AtomicInteger count = new AtomicInteger(0);
        private final AtomicLong windowStart = new AtomicLong(System.currentTimeMillis());
        
        public boolean tryAcquire(long currentTime, int maxRequests, long timeWindow) {
            long windowStartTime = windowStart.get();
            
            // Reset window if expired
            if (currentTime - windowStartTime > timeWindow) {
                if (windowStart.compareAndSet(windowStartTime, currentTime)) {
                    count.set(0);
                }
            }
            
            // Check if within limit
            int currentCount = count.incrementAndGet();
            return currentCount <= maxRequests;
        }
        
        public int getCount() {
            return count.get();
        }
        
        public long getWindowStart() {
            return windowStart.get();
        }
    }
}
