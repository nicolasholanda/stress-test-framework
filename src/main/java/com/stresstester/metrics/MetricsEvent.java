package com.stresstester.metrics;

public class MetricsEvent {

    public enum Type { REQUEST_SUCCESS, REQUEST_FAILURE }

    private final Type type;
    private final long latencyMs;
    private final int statusCode;
    private final String error;

    private MetricsEvent(Type type, long latencyMs, int statusCode, String error) {
        this.type = type;
        this.latencyMs = latencyMs;
        this.statusCode = statusCode;
        this.error = error;
    }

    public static MetricsEvent success(long latencyMs, int statusCode) {
        return new MetricsEvent(Type.REQUEST_SUCCESS, latencyMs, statusCode, null);
    }

    public static MetricsEvent failure(String error) {
        return new MetricsEvent(Type.REQUEST_FAILURE, 0, 0, error);
    }

    public Type getType() { return type; }
    public long getLatencyMs() { return latencyMs; }
    public int getStatusCode() { return statusCode; }
    public String getError() { return error; }
}
