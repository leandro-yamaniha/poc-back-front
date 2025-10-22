package com.beautysalon.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Rate Limit Configuration Properties
 * Controls rate limiting behavior through application.yml
 */
@Component
@ConfigurationProperties(prefix = "app.rate-limit")
public class RateLimitProperties {

    /**
     * Enable or disable rate limiting
     * Default: false (disabled)
     */
    private boolean enabled = false;

    /**
     * Maximum requests per minute per IP
     * Default: 100
     */
    private int maxRequestsPerMinute = 100;

    /**
     * Time window in milliseconds
     * Default: 60000 (1 minute)
     */
    private long timeWindowMs = 60_000;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getMaxRequestsPerMinute() {
        return maxRequestsPerMinute;
    }

    public void setMaxRequestsPerMinute(int maxRequestsPerMinute) {
        this.maxRequestsPerMinute = maxRequestsPerMinute;
    }

    public long getTimeWindowMs() {
        return timeWindowMs;
    }

    public void setTimeWindowMs(long timeWindowMs) {
        this.timeWindowMs = timeWindowMs;
    }
}
