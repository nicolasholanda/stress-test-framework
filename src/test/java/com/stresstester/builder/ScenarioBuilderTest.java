package com.stresstester.builder;

import com.stresstester.config.TestConfig;
import com.stresstester.exception.ConfigurationException;
import com.stresstester.model.HttpMethod;
import com.stresstester.model.LoadProfile;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class ScenarioBuilderTest {

    @Test
    void buildsValidConfig() {
        TestConfig config = new ScenarioBuilder()
                .url("https://example.com")
                .method(HttpMethod.POST)
                .body("{\"key\":\"value\"}")
                .virtualUsers(5)
                .duration(Duration.ofSeconds(10))
                .build();

        assertEquals("https://example.com", config.getRequestConfig().getUrl());
        assertEquals(HttpMethod.POST, config.getRequestConfig().getMethod());
        assertEquals("{\"key\":\"value\"}", config.getRequestConfig().getBody());
        assertEquals(5, config.getVirtualUsers());
        assertEquals(Duration.ofSeconds(10), config.getDuration());
    }

    @Test
    void appliesDefaults() {
        TestConfig config = new ScenarioBuilder()
                .url("https://example.com")
                .duration(Duration.ofSeconds(5))
                .build();

        assertEquals(HttpMethod.GET, config.getRequestConfig().getMethod());
        assertEquals(LoadProfile.CONSTANT, config.getLoadProfile());
        assertEquals(1, config.getVirtualUsers());
        assertEquals(Duration.ofSeconds(30), config.getRequestTimeout());
    }

    @Test
    void throwsOnBlankUrl() {
        assertThrows(ConfigurationException.class, () ->
                new ScenarioBuilder().url("").duration(Duration.ofSeconds(5)).build());
    }

    @Test
    void throwsOnNullUrl() {
        assertThrows(ConfigurationException.class, () ->
                new ScenarioBuilder().duration(Duration.ofSeconds(5)).build());
    }

    @Test
    void throwsWhenNeitherDurationNorTotalRequestsSet() {
        assertThrows(ConfigurationException.class, () ->
                new ScenarioBuilder().url("https://example.com").build());
    }

    @Test
    void throwsOnZeroVirtualUsers() {
        assertThrows(ConfigurationException.class, () ->
                new ScenarioBuilder().url("https://example.com").virtualUsers(0).duration(Duration.ofSeconds(5)).build());
    }

    @Test
    void throwsOnRampUpProfileWithoutRampUpSeconds() {
        assertThrows(ConfigurationException.class, () ->
                new ScenarioBuilder()
                        .url("https://example.com")
                        .duration(Duration.ofSeconds(10))
                        .loadProfile(LoadProfile.RAMP_UP)
                        .build());
    }

    @Test
    void addsHeadersCorrectly() {
        TestConfig config = new ScenarioBuilder()
                .url("https://example.com")
                .header("Authorization", "Bearer token")
                .header("Content-Type", "application/json")
                .duration(Duration.ofSeconds(5))
                .build();

        assertEquals("Bearer token", config.getRequestConfig().getHeaders().get("Authorization"));
        assertEquals("application/json", config.getRequestConfig().getHeaders().get("Content-Type"));
    }

    @Test
    void acceptsTotalRequestsInsteadOfDuration() {
        TestConfig config = new ScenarioBuilder()
                .url("https://example.com")
                .totalRequests(100)
                .build();

        assertEquals(100, config.getTotalRequests());
        assertFalse(config.isTimeBased());
    }

    @Test
    void isTimeBasedWhenDurationSet() {
        TestConfig config = new ScenarioBuilder()
                .url("https://example.com")
                .duration(Duration.ofSeconds(30))
                .build();

        assertTrue(config.isTimeBased());
    }
}
