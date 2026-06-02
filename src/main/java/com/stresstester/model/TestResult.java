package com.stresstester.model;

import java.time.Instant;
import java.util.Map;

public class TestResult {

    private final Instant startTime;
    private final Instant endTime;
    private final long totalRequests;
    private final long successfulRequests;
    private final long failedRequests;
    private final long minLatencyMs;
    private final long maxLatencyMs;
    private final double avgLatencyMs;
    private final long p50LatencyMs;
    private final long p90LatencyMs;
    private final long p95LatencyMs;
    private final long p99LatencyMs;
    private final double requestsPerSecond;
    private final Map<Integer, Long> statusCodeCounts;

    public TestResult(
            Instant startTime,
            Instant endTime,
            long totalRequests,
            long successfulRequests,
            long failedRequests,
            long minLatencyMs,
            long maxLatencyMs,
            double avgLatencyMs,
            long p50LatencyMs,
            long p90LatencyMs,
            long p95LatencyMs,
            long p99LatencyMs,
            double requestsPerSecond,
            Map<Integer, Long> statusCodeCounts) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.totalRequests = totalRequests;
        this.successfulRequests = successfulRequests;
        this.failedRequests = failedRequests;
        this.minLatencyMs = minLatencyMs;
        this.maxLatencyMs = maxLatencyMs;
        this.avgLatencyMs = avgLatencyMs;
        this.p50LatencyMs = p50LatencyMs;
        this.p90LatencyMs = p90LatencyMs;
        this.p95LatencyMs = p95LatencyMs;
        this.p99LatencyMs = p99LatencyMs;
        this.requestsPerSecond = requestsPerSecond;
        this.statusCodeCounts = statusCodeCounts;
    }

    public Instant getStartTime() { return startTime; }
    public Instant getEndTime() { return endTime; }
    public long getTotalRequests() { return totalRequests; }
    public long getSuccessfulRequests() { return successfulRequests; }
    public long getFailedRequests() { return failedRequests; }
    public long getMinLatencyMs() { return minLatencyMs; }
    public long getMaxLatencyMs() { return maxLatencyMs; }
    public double getAvgLatencyMs() { return avgLatencyMs; }
    public long getP50LatencyMs() { return p50LatencyMs; }
    public long getP90LatencyMs() { return p90LatencyMs; }
    public long getP95LatencyMs() { return p95LatencyMs; }
    public long getP99LatencyMs() { return p99LatencyMs; }
    public double getRequestsPerSecond() { return requestsPerSecond; }
    public Map<Integer, Long> getStatusCodeCounts() { return statusCodeCounts; }
}
