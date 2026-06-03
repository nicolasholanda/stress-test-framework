package com.stresstester.load;

import com.stresstester.config.HttpRequestConfig;
import com.stresstester.config.TestConfig;
import com.stresstester.model.HttpMethod;
import com.stresstester.model.LoadProfile;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class LoadStrategyTest {

    private TestConfig timeBased(int users) {
        return new TestConfig(
                new HttpRequestConfig("https://example.com", HttpMethod.GET, Collections.emptyMap(), null),
                users, Duration.ofSeconds(10), 0, LoadProfile.CONSTANT, 0, Duration.ofSeconds(30));
    }

    private TestConfig countBased(int users, int totalRequests) {
        return new TestConfig(
                new HttpRequestConfig("https://example.com", HttpMethod.GET, Collections.emptyMap(), null),
                users, null, totalRequests, LoadProfile.CONSTANT, 0, Duration.ofSeconds(30));
    }

    private TestConfig rampUp(int users, int rampUpSeconds) {
        return new TestConfig(
                new HttpRequestConfig("https://example.com", HttpMethod.GET, Collections.emptyMap(), null),
                users, Duration.ofSeconds(20), 0, LoadProfile.RAMP_UP, rampUpSeconds, Duration.ofSeconds(30));
    }

    @Test
    void constantAlwaysReturnsFullUsers() {
        ConstantLoadStrategy strategy = new ConstantLoadStrategy();
        TestConfig config = timeBased(10);
        assertEquals(10, strategy.getActiveUsers(0, config));
        assertEquals(10, strategy.getActiveUsers(5000, config));
        assertEquals(10, strategy.getActiveUsers(9999, config));
    }

    @Test
    void constantIsDoneWhenDurationElapsed() {
        ConstantLoadStrategy strategy = new ConstantLoadStrategy();
        TestConfig config = timeBased(5);
        assertFalse(strategy.isDone(9999, 0, config));
        assertTrue(strategy.isDone(10000, 0, config));
        assertTrue(strategy.isDone(15000, 0, config));
    }

    @Test
    void constantIsDoneWhenRequestCountReached() {
        ConstantLoadStrategy strategy = new ConstantLoadStrategy();
        TestConfig config = countBased(5, 100);
        assertFalse(strategy.isDone(0, 99, config));
        assertTrue(strategy.isDone(0, 100, config));
        assertTrue(strategy.isDone(0, 200, config));
    }

    @Test
    void rampUpStartsWithAtLeastOneUser() {
        RampUpLoadStrategy strategy = new RampUpLoadStrategy();
        TestConfig config = rampUp(10, 10);
        assertTrue(strategy.getActiveUsers(0, config) >= 1);
    }

    @Test
    void rampUpGraduallyIncreasesUsers() {
        RampUpLoadStrategy strategy = new RampUpLoadStrategy();
        TestConfig config = rampUp(10, 10);
        int at0    = strategy.getActiveUsers(0, config);
        int at5000 = strategy.getActiveUsers(5000, config);
        int at10000 = strategy.getActiveUsers(10000, config);
        assertTrue(at5000 > at0);
        assertEquals(10, at10000);
    }

    @Test
    void rampUpReturnsFullUsersAfterRampPeriod() {
        RampUpLoadStrategy strategy = new RampUpLoadStrategy();
        TestConfig config = rampUp(8, 5);
        assertEquals(8, strategy.getActiveUsers(5000, config));
        assertEquals(8, strategy.getActiveUsers(15000, config));
    }

    @Test
    void spikeReturnsBaseUsersBeforeSpikeTrigger() {
        SpikeLoadStrategy strategy = new SpikeLoadStrategy();
        TestConfig config = timeBased(9);
        int expected = Math.max(1, 9 / 3);
        assertEquals(expected, strategy.getActiveUsers(0, config));
        assertEquals(expected, strategy.getActiveUsers(4999, config));
    }

    @Test
    void spikeReturnsFullUsersAfterSpikeTrigger() {
        SpikeLoadStrategy strategy = new SpikeLoadStrategy();
        TestConfig config = timeBased(9);
        assertEquals(9, strategy.getActiveUsers(5000, config));
        assertEquals(9, strategy.getActiveUsers(9000, config));
    }

    @Test
    void spikeIsDoneWhenDurationElapsed() {
        SpikeLoadStrategy strategy = new SpikeLoadStrategy();
        TestConfig config = timeBased(5);
        assertFalse(strategy.isDone(9999, 0, config));
        assertTrue(strategy.isDone(10000, 0, config));
    }
}
