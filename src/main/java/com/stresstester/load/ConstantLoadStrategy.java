package com.stresstester.load;

import com.stresstester.config.TestConfig;

public class ConstantLoadStrategy implements LoadStrategy {

    @Override
    public int getActiveUsers(long elapsedMs, TestConfig config) {
        return config.getVirtualUsers();
    }

    @Override
    public boolean isDone(long elapsedMs, long completedRequests, TestConfig config) {
        if (config.isTimeBased()) {
            return elapsedMs >= config.getDuration().toMillis();
        }
        return completedRequests >= config.getTotalRequests();
    }
}
