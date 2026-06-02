package com.stresstester.load;

import com.stresstester.config.TestConfig;

public class SpikeLoadStrategy implements LoadStrategy {

    private static final long DEFAULT_BASE_DURATION_MS = 10_000;

    @Override
    public int getActiveUsers(long elapsedMs, TestConfig config) {
        int baseUsers = Math.max(1, config.getVirtualUsers() / 3);
        long spikeTriggerMs = config.isTimeBased()
                ? config.getDuration().toMillis() / 2
                : DEFAULT_BASE_DURATION_MS;
        return elapsedMs < spikeTriggerMs ? baseUsers : config.getVirtualUsers();
    }

    @Override
    public boolean isDone(long elapsedMs, long completedRequests, TestConfig config) {
        if (config.isTimeBased()) {
            return elapsedMs >= config.getDuration().toMillis();
        }
        return completedRequests >= config.getTotalRequests();
    }
}
