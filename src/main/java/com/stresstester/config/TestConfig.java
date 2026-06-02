package com.stresstester.config;

import com.stresstester.model.LoadProfile;
import java.time.Duration;

public class TestConfig {

    private final HttpRequestConfig requestConfig;
    private final int virtualUsers;
    private final Duration duration;
    private final int totalRequests;
    private final LoadProfile loadProfile;
    private final int rampUpSeconds;
    private final Duration requestTimeout;

    public TestConfig(
            HttpRequestConfig requestConfig,
            int virtualUsers,
            Duration duration,
            int totalRequests,
            LoadProfile loadProfile,
            int rampUpSeconds,
            Duration requestTimeout) {
        this.requestConfig = requestConfig;
        this.virtualUsers = virtualUsers;
        this.duration = duration;
        this.totalRequests = totalRequests;
        this.loadProfile = loadProfile;
        this.rampUpSeconds = rampUpSeconds;
        this.requestTimeout = requestTimeout;
    }

    public HttpRequestConfig getRequestConfig() { return requestConfig; }
    public int getVirtualUsers() { return virtualUsers; }
    public Duration getDuration() { return duration; }
    public int getTotalRequests() { return totalRequests; }
    public LoadProfile getLoadProfile() { return loadProfile; }
    public int getRampUpSeconds() { return rampUpSeconds; }
    public Duration getRequestTimeout() { return requestTimeout; }

    public boolean isTimeBased() {
        return duration != null && !duration.isZero();
    }
}
