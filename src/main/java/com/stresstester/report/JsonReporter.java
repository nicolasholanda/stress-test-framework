package com.stresstester.report;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.stresstester.exception.StressTestException;
import com.stresstester.model.TestResult;

import java.io.IOException;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

public class JsonReporter implements Reporter {

    private final Path outputPath;
    private final ObjectMapper mapper;

    public JsonReporter(Path outputPath) {
        this.outputPath = outputPath;
        this.mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
    }

    @Override
    public void report(TestResult result) {
        try {
            mapper.writeValue(outputPath.toFile(), toMap(result));
        } catch (IOException e) {
            throw new StressTestException("Failed to write JSON report to " + outputPath, e);
        }
    }

    private Map<String, Object> toMap(TestResult result) {
        Map<String, Object> latency = new LinkedHashMap<>();
        latency.put("minMs",    result.getMinLatencyMs());
        latency.put("avgMs",    result.getAvgLatencyMs());
        latency.put("p50Ms",    result.getP50LatencyMs());
        latency.put("p90Ms",    result.getP90LatencyMs());
        latency.put("p95Ms",    result.getP95LatencyMs());
        latency.put("p99Ms",    result.getP99LatencyMs());
        latency.put("maxMs",    result.getMaxLatencyMs());

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("startTime",         result.getStartTime().toString());
        map.put("endTime",           result.getEndTime().toString());
        map.put("totalRequests",     result.getTotalRequests());
        map.put("successfulRequests", result.getSuccessfulRequests());
        map.put("failedRequests",    result.getFailedRequests());
        map.put("requestsPerSecond", result.getRequestsPerSecond());
        map.put("latency",           latency);
        map.put("statusCodes",       result.getStatusCodeCounts());
        return map;
    }
}
