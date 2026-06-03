package com.stresstester.cli;

import com.stresstester.builder.ScenarioBuilder;
import com.stresstester.config.TestConfig;
import com.stresstester.core.TestOrchestrator;
import com.stresstester.model.HttpMethod;
import com.stresstester.model.LoadProfile;
import com.stresstester.model.TestResult;
import com.stresstester.report.ConsoleReporter;
import com.stresstester.report.JsonReporter;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Command(
        name = "stress-test",
        description = "Run an HTTP stress test against a target endpoint.",
        mixinStandardHelpOptions = true,
        version = "1.0.0"
)
public class StressTestCli implements Runnable {

    @Option(names = {"--url", "-u"}, required = true, description = "Target URL")
    private String url;

    @Option(names = {"--method", "-m"}, description = "HTTP method (default: GET)")
    private HttpMethod method = HttpMethod.GET;

    @Option(names = {"--header", "-H"}, description = "Request header in 'Name: Value' format (repeatable)")
    private List<String> headers = new ArrayList<>();

    @Option(names = {"--body", "-b"}, description = "Request body")
    private String body;

    @Option(names = {"--users", "-c"}, description = "Number of virtual users (default: 1)")
    private int virtualUsers = 1;

    @Option(names = {"--duration", "-d"}, description = "Test duration in seconds")
    private int durationSeconds;

    @Option(names = {"--requests", "-n"}, description = "Total number of requests")
    private int totalRequests;

    @Option(names = {"--load-profile", "-p"}, description = "Load profile: CONSTANT, RAMP_UP, SPIKE (default: CONSTANT)")
    private LoadProfile loadProfile = LoadProfile.CONSTANT;

    @Option(names = {"--ramp-up", "-r"}, description = "Ramp-up duration in seconds (required for RAMP_UP profile)")
    private int rampUpSeconds;

    @Option(names = {"--timeout", "-t"}, description = "Request timeout in seconds (default: 30)")
    private int timeoutSeconds = 30;

    @Option(names = {"--output", "-o"}, description = "Write JSON report to this file path")
    private Path outputPath;

    @Override
    public void run() {
        ScenarioBuilder builder = new ScenarioBuilder()
                .url(url)
                .method(method)
                .virtualUsers(virtualUsers)
                .loadProfile(loadProfile)
                .rampUpSeconds(rampUpSeconds)
                .requestTimeout(Duration.ofSeconds(timeoutSeconds));

        if (body != null) {
            builder.body(body);
        }
        if (durationSeconds > 0) {
            builder.duration(Duration.ofSeconds(durationSeconds));
        }
        if (totalRequests > 0) {
            builder.totalRequests(totalRequests);
        }
        for (String header : headers) {
            int colon = header.indexOf(':');
            if (colon > 0) {
                builder.header(header.substring(0, colon).trim(), header.substring(colon + 1).trim());
            }
        }

        TestConfig config = builder.build();
        TestResult result = new TestOrchestrator().run(config);

        new ConsoleReporter().report(result);

        if (outputPath != null) {
            new JsonReporter(outputPath).report(result);
            System.out.println("JSON report written to: " + outputPath);
        }
    }
}
