package com.stresstester.metrics;

public interface MetricsListener {

    void onEvent(MetricsEvent event);
}
