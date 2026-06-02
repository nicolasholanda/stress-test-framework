package com.stresstester.metrics;

import com.stresstester.model.TestResult;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class MetricsCollector implements MetricsListener {

    private final AtomicLong successCount = new AtomicLong(0);
    private final AtomicLong failureCount = new AtomicLong(0);
    private final LatencyStats latencyStats = new LatencyStats();
    private final ConcurrentHashMap<Integer, AtomicLong> statusCodeCounts = new ConcurrentHashMap<>();
    private final Instant startTime = Instant.now();
    private volatile Instant endTime;

    @Override
    public void onEvent(MetricsEvent event) {
        if (event.getType() == MetricsEvent.Type.REQUEST_SUCCESS) {
            successCount.incrementAndGet();
            latencyStats.record(event.getLatencyMs());
            statusCodeCounts.computeIfAbsent(event.getStatusCode(), k -> new AtomicLong(0)).incrementAndGet();
        } else {
            failureCount.incrementAndGet();
        }
    }

    public void finish() {
        endTime = Instant.now();
    }

    public long getTotalCompletedRequests() {
        return successCount.get() + failureCount.get();
    }

    public TestResult buildResult() {
        Instant end = endTime != null ? endTime : Instant.now();
        long durationMs = Duration.between(startTime, end).toMillis();
        long total = getTotalCompletedRequests();
        double rps = durationMs > 0 ? total / (durationMs / 1000.0) : 0.0;
        boolean hasLatency = latencyStats.getCount() > 0;

        Map<Integer, Long> statusCounts = new HashMap<>();
        statusCodeCounts.forEach((k, v) -> statusCounts.put(k, v.get()));

        return new TestResult(
                startTime,
                end,
                total,
                successCount.get(),
                failureCount.get(),
                hasLatency ? latencyStats.getMin() : 0,
                hasLatency ? latencyStats.getMax() : 0,
                hasLatency ? latencyStats.getMean() : 0.0,
                hasLatency ? latencyStats.getPercentile(50) : 0,
                hasLatency ? latencyStats.getPercentile(90) : 0,
                hasLatency ? latencyStats.getPercentile(95) : 0,
                hasLatency ? latencyStats.getPercentile(99) : 0,
                rps,
                statusCounts
        );
    }
}
