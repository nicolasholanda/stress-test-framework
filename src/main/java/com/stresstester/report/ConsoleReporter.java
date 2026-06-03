package com.stresstester.report;

import com.stresstester.model.TestResult;

import java.time.Duration;
import java.util.Map;

public class ConsoleReporter implements Reporter {

    private static final String LINE  = "=================================================";
    private static final String DIVIDER = "-------------------------------------------------";

    @Override
    public void report(TestResult result) {
        long totalMs = Duration.between(result.getStartTime(), result.getEndTime()).toMillis();
        double durationSec = totalMs / 1000.0;
        long total = result.getTotalRequests();
        double successPct = total > 0 ? (result.getSuccessfulRequests() * 100.0) / total : 0.0;
        double failPct    = total > 0 ? (result.getFailedRequests() * 100.0) / total : 0.0;

        System.out.println(LINE);
        System.out.println("  HTTP STRESS TEST RESULTS");
        System.out.println(LINE);
        System.out.printf("  %-22s %.2f s%n",       "Duration",        durationSec);
        System.out.printf("  %-22s %,d%n",           "Total Requests",  total);
        System.out.printf("  %-22s %,d (%.2f%%)%n", "Successful",      result.getSuccessfulRequests(), successPct);
        System.out.printf("  %-22s %,d (%.2f%%)%n", "Failed",          result.getFailedRequests(),    failPct);
        System.out.printf("  %-22s %.2f%n",          "Requests/sec",    result.getRequestsPerSecond());
        System.out.println(DIVIDER);
        System.out.println("  Latency (ms)");
        System.out.printf("  %-22s %d%n",   "  Min",         result.getMinLatencyMs());
        System.out.printf("  %-22s %.2f%n", "  Avg",         result.getAvgLatencyMs());
        System.out.printf("  %-22s %d%n",   "  Median (p50)", result.getP50LatencyMs());
        System.out.printf("  %-22s %d%n",   "  p90",         result.getP90LatencyMs());
        System.out.printf("  %-22s %d%n",   "  p95",         result.getP95LatencyMs());
        System.out.printf("  %-22s %d%n",   "  p99",         result.getP99LatencyMs());
        System.out.printf("  %-22s %d%n",   "  Max",         result.getMaxLatencyMs());
        System.out.println(DIVIDER);
        System.out.println("  Status Codes");
        for (Map.Entry<Integer, Long> entry : result.getStatusCodeCounts().entrySet()) {
            System.out.printf("  %-22s %,d%n", "  " + entry.getKey(), entry.getValue());
        }
        System.out.println(LINE);
    }
}
