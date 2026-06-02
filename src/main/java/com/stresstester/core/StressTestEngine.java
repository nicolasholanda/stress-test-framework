package com.stresstester.core;

import com.stresstester.config.TestConfig;
import com.stresstester.model.TestResult;

public abstract class StressTestEngine {

    protected abstract void setup(TestConfig config);

    protected abstract void executeLoad(TestConfig config);

    protected abstract TestResult collectResults();

    protected void teardown() {}

    public final TestResult run(TestConfig config) {
        setup(config);
        try {
            executeLoad(config);
        } finally {
            teardown();
        }
        return collectResults();
    }
}
