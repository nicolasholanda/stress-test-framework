package com.stresstester;

import com.stresstester.cli.StressTestCli;
import picocli.CommandLine;

public class Main {

    public static void main(String[] args) {
        int exitCode = new CommandLine(new StressTestCli()).execute(args);
        System.exit(exitCode);
    }
}
