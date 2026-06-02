package com.stresstester.metrics;

import org.HdrHistogram.SynchronizedHistogram;

public class LatencyStats {

    private final SynchronizedHistogram histogram;

    public LatencyStats() {
        this.histogram = new SynchronizedHistogram(3);
    }

    public void record(long latencyMs) {
        histogram.recordValue(Math.max(1, latencyMs));
    }

    public long getPercentile(double percentile) {
        return histogram.getValueAtPercentile(percentile);
    }

    public long getMin() { return histogram.getMinValue(); }
    public long getMax() { return histogram.getMaxValue(); }
    public double getMean() { return histogram.getMean(); }
    public long getCount() { return histogram.getTotalCount(); }
}
