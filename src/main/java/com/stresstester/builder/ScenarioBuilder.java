package com.stresstester.builder;

import com.stresstester.config.HttpRequestConfig;
import com.stresstester.config.TestConfig;
import com.stresstester.exception.ConfigurationException;
import com.stresstester.model.HttpMethod;
import com.stresstester.model.LoadProfile;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ScenarioBuilder {

    private String url;
    private HttpMethod method = HttpMethod.GET;
    private Map<String, String> headers = new HashMap<>();
    private String body;
    private int virtualUsers = 1;
    private Duration duration;
    private int totalRequests;
    private LoadProfile loadProfile = LoadProfile.CONSTANT;
    private int rampUpSeconds;
    private Duration requestTimeout = Duration.ofSeconds(30);

    public ScenarioBuilder url(String url) {
        this.url = url;
        return this;
    }

    public ScenarioBuilder method(HttpMethod method) {
        this.method = method;
        return this;
    }

    public ScenarioBuilder header(String name, String value) {
        this.headers.put(name, value);
        return this;
    }

    public ScenarioBuilder headers(Map<String, String> headers) {
        this.headers.putAll(headers);
        return this;
    }

    public ScenarioBuilder body(String body) {
        this.body = body;
        return this;
    }

    public ScenarioBuilder virtualUsers(int virtualUsers) {
        this.virtualUsers = virtualUsers;
        return this;
    }

    public ScenarioBuilder duration(Duration duration) {
        this.duration = duration;
        return this;
    }

    public ScenarioBuilder totalRequests(int totalRequests) {
        this.totalRequests = totalRequests;
        return this;
    }

    public ScenarioBuilder loadProfile(LoadProfile loadProfile) {
        this.loadProfile = loadProfile;
        return this;
    }

    public ScenarioBuilder rampUpSeconds(int rampUpSeconds) {
        this.rampUpSeconds = rampUpSeconds;
        return this;
    }

    public ScenarioBuilder requestTimeout(Duration requestTimeout) {
        this.requestTimeout = requestTimeout;
        return this;
    }

    public TestConfig build() {
        validate();
        HttpRequestConfig requestConfig = new HttpRequestConfig(
                url, method, Collections.unmodifiableMap(new HashMap<>(headers)), body
        );
        return new TestConfig(
                requestConfig, virtualUsers, duration, totalRequests,
                loadProfile, rampUpSeconds, requestTimeout
        );
    }

    private void validate() {
        if (url == null || url.isBlank()) {
            throw new ConfigurationException("URL must not be blank");
        }
        if (virtualUsers < 1) {
            throw new ConfigurationException("virtualUsers must be at least 1");
        }
        boolean hasDuration = duration != null && !duration.isZero();
        boolean hasRequests = totalRequests > 0;
        if (!hasDuration && !hasRequests) {
            throw new ConfigurationException("Either duration or totalRequests must be set");
        }
        if (requestTimeout == null || requestTimeout.isZero()) {
            throw new ConfigurationException("requestTimeout must be a positive duration");
        }
        if (loadProfile == LoadProfile.RAMP_UP && rampUpSeconds <= 0) {
            throw new ConfigurationException("rampUpSeconds must be positive when using RAMP_UP profile");
        }
    }
}
