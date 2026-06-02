package com.stresstester.load;

import com.stresstester.config.TestConfig;

public interface LoadStrategy {

    int getActiveUsers(long elapsedMs, TestConfig config);

    boolean isDone(long elapsedMs, long completedRequests, TestConfig config);
}
