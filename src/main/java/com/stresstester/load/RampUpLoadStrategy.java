package com.stresstester.load;

import com.stresstester.config.TestConfig;

public class RampUpLoadStrategy implements LoadStrategy {

    @Override
    public int getActiveUsers(long elapsedMs, TestConfig config) {
        int virtualUsers = config.getVirtualUsers();
        long rampUpMs = (long) config.getRampUpSeconds() * 1000;
        if (rampUpMs <= 0 || elapsedMs >= rampUpMs) {
            return virtualUsers;
        }
        return Math.max(1, (int) Math.ceil((double) virtualUsers * elapsedMs / rampUpMs));
    }

    @Override
    public boolean isDone(long elapsedMs, long completedRequests, TestConfig config) {
        if (config.isTimeBased()) {
            return elapsedMs >= config.getDuration().toMillis();
        }
        return completedRequests >= config.getTotalRequests();
    }
}
