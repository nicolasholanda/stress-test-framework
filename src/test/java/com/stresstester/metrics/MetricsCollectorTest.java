package com.stresstester.metrics;

import com.stresstester.model.TestResult;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MetricsCollectorTest {

    @Test
    void countsSuccessesAndFailures() {
        MetricsCollector collector = new MetricsCollector();
        collector.onEvent(MetricsEvent.success(100, 200));
        collector.onEvent(MetricsEvent.success(150, 200));
        collector.onEvent(MetricsEvent.failure("timeout"));

        assertEquals(3, collector.getTotalCompletedRequests());
    }

    @Test
    void buildsResultWithCorrectCounts() {
        MetricsCollector collector = new MetricsCollector();
        collector.onEvent(MetricsEvent.success(100, 200));
        collector.onEvent(MetricsEvent.success(200, 200));
        collector.onEvent(MetricsEvent.failure("connection refused"));
        collector.finish();

        TestResult result = collector.buildResult();

        assertEquals(3, result.getTotalRequests());
        assertEquals(2, result.getSuccessfulRequests());
        assertEquals(1, result.getFailedRequests());
    }

    @Test
    void tracksLatencyStats() {
        MetricsCollector collector = new MetricsCollector();
        collector.onEvent(MetricsEvent.success(50, 200));
        collector.onEvent(MetricsEvent.success(100, 200));
        collector.onEvent(MetricsEvent.success(150, 200));
        collector.finish();

        TestResult result = collector.buildResult();

        assertEquals(50, result.getMinLatencyMs());
        assertEquals(150, result.getMaxLatencyMs());
        assertTrue(result.getAvgLatencyMs() > 0);
        assertTrue(result.getP50LatencyMs() > 0);
    }

    @Test
    void tracksStatusCodeCounts() {
        MetricsCollector collector = new MetricsCollector();
        collector.onEvent(MetricsEvent.success(100, 200));
        collector.onEvent(MetricsEvent.success(100, 200));
        collector.onEvent(MetricsEvent.success(100, 404));
        collector.finish();

        TestResult result = collector.buildResult();

        assertEquals(2L, result.getStatusCodeCounts().get(200));
        assertEquals(1L, result.getStatusCodeCounts().get(404));
    }

    @Test
    void requestsPerSecondIsPositive() {
        MetricsCollector collector = new MetricsCollector();
        for (int i = 0; i < 10; i++) {
            collector.onEvent(MetricsEvent.success(10, 200));
        }
        collector.finish();

        assertTrue(collector.buildResult().getRequestsPerSecond() > 0);
    }

    @Test
    void emptyCollectorProducesZeroResult() {
        MetricsCollector collector = new MetricsCollector();
        collector.finish();

        TestResult result = collector.buildResult();

        assertEquals(0, result.getTotalRequests());
        assertEquals(0, result.getMinLatencyMs());
        assertEquals(0, result.getMaxLatencyMs());
        assertEquals(0.0, result.getAvgLatencyMs());
    }

    @Test
    void failureEventsDoNotRecordLatency() {
        MetricsCollector collector = new MetricsCollector();
        collector.onEvent(MetricsEvent.failure("timeout"));
        collector.finish();

        TestResult result = collector.buildResult();

        assertEquals(1, result.getFailedRequests());
        assertEquals(0, result.getMinLatencyMs());
    }
}
