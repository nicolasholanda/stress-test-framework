package com.stresstester.integration;

import com.stresstester.builder.ScenarioBuilder;
import com.stresstester.config.TestConfig;
import com.stresstester.core.TestOrchestrator;
import com.stresstester.model.HttpMethod;
import com.stresstester.model.LoadProfile;
import com.stresstester.model.TestResult;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class StressTestEngineIntegrationTest {

    private MockWebServer server;

    @BeforeEach
    void setUp() throws IOException {
        server = new MockWebServer();
        server.setDispatcher(new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) {
                return new MockResponse().setResponseCode(200).setBody("ok");
            }
        });
        server.start();
    }

    @AfterEach
    void tearDown() throws IOException {
        server.shutdown();
    }

    @Test
    void completesExactRequestCount() {
        TestConfig config = new ScenarioBuilder()
                .url(server.url("/api").toString())
                .virtualUsers(2)
                .totalRequests(10)
                .build();

        TestResult result = new TestOrchestrator().run(config);

        assertEquals(10, result.getTotalRequests());
        assertEquals(10, result.getSuccessfulRequests());
        assertEquals(0, result.getFailedRequests());
        assertTrue(result.getRequestsPerSecond() > 0);
        assertTrue(result.getStatusCodeCounts().containsKey(200));
        assertEquals(10L, result.getStatusCodeCounts().get(200));
    }

    @Test
    void runsForConfiguredDuration() {
        TestConfig config = new ScenarioBuilder()
                .url(server.url("/timed").toString())
                .virtualUsers(3)
                .duration(Duration.ofSeconds(2))
                .build();

        Instant before = Instant.now();
        TestResult result = new TestOrchestrator().run(config);
        long elapsedMs = Duration.between(before, Instant.now()).toMillis();

        assertTrue(result.getTotalRequests() > 0);
        assertTrue(elapsedMs >= 2000, "Expected at least 2000ms elapsed, was: " + elapsedMs);
    }

    @Test
    void tracksLatencyPercentiles() {
        TestConfig config = new ScenarioBuilder()
                .url(server.url("/latency").toString())
                .virtualUsers(1)
                .totalRequests(20)
                .build();

        TestResult result = new TestOrchestrator().run(config);

        assertTrue(result.getMinLatencyMs() >= 0);
        assertTrue(result.getMaxLatencyMs() >= result.getMinLatencyMs());
        assertTrue(result.getP50LatencyMs() >= result.getMinLatencyMs());
        assertTrue(result.getP99LatencyMs() >= result.getP50LatencyMs());
    }

    @Test
    void handlesPostRequestWithBody() {
        TestConfig config = new ScenarioBuilder()
                .url(server.url("/create").toString())
                .method(HttpMethod.POST)
                .body("{\"name\":\"stress-test\"}")
                .header("Content-Type", "application/json")
                .virtualUsers(1)
                .totalRequests(5)
                .build();

        TestResult result = new TestOrchestrator().run(config);

        assertEquals(5, result.getTotalRequests());
        assertEquals(5, result.getSuccessfulRequests());
    }

    @Test
    void serverErrorsCountAsSuccessfulHttpExchanges() {
        server.setDispatcher(new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) {
                return new MockResponse().setResponseCode(503).setBody("unavailable");
            }
        });

        TestConfig config = new ScenarioBuilder()
                .url(server.url("/down").toString())
                .virtualUsers(1)
                .totalRequests(5)
                .build();

        TestResult result = new TestOrchestrator().run(config);

        assertEquals(5, result.getTotalRequests());
        assertEquals(5, result.getSuccessfulRequests());
        assertEquals(0, result.getFailedRequests());
        assertTrue(result.getStatusCodeCounts().containsKey(503));
    }

    @Test
    void rampUpProfileReachesFullConcurrency() {
        TestConfig config = new ScenarioBuilder()
                .url(server.url("/ramp").toString())
                .virtualUsers(4)
                .duration(Duration.ofSeconds(3))
                .loadProfile(LoadProfile.RAMP_UP)
                .rampUpSeconds(2)
                .build();

        TestResult result = new TestOrchestrator().run(config);

        assertTrue(result.getTotalRequests() > 0);
        assertEquals(0, result.getFailedRequests());
    }
}
