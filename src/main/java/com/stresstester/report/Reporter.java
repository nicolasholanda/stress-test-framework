package com.stresstester.report;

import com.stresstester.model.TestResult;

public interface Reporter {

    void report(TestResult result);
}
