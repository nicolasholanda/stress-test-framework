# stress-test-framework

An HTTP stress testing framework written in Java. Point it at an endpoint, configure how many virtual users to simulate and for how long, and get back latency percentiles, throughput, and error counts.

## Tech stack

- Java 21 + Maven
- OkHttp 4.12 — HTTP execution
- HdrHistogram 2.2 — lock-free latency percentile tracking
- Picocli 4.7 — CLI
- Jackson 2.17 — JSON reports
- JUnit 5 + OkHttp MockWebServer — tests

## Design patterns

| Pattern | Where |
|---|---|
| Builder | `ScenarioBuilder` — fluent test configuration |
| Strategy | `LoadStrategy` — pluggable load profiles |
| Command | `HttpCommand` — encapsulates a single HTTP request |
| Observer | `MetricsListener` / `MetricsCollector` — event-driven metrics |
| Factory | `HttpClientFactory`, `LoadStrategyFactory` |
| Template Method | `StressTestEngine` — fixed run lifecycle, overridable steps |

## Load profiles

- **CONSTANT** — all virtual users active from the start
- **RAMP_UP** — users added gradually over `--ramp-up` seconds
- **SPIKE** — starts at 1/3 of users, jumps to the full count at the halfway point

## Build

```bash
mvn package
```

This produces `target/stress-test-framework-1.0.0.jar` (fat JAR).

## CLI usage

```bash
java -jar target/stress-test-framework-1.0.0.jar [options]
```

| Option | Description |
|---|---|
| `--url`, `-u` | Target URL (required) |
| `--method`, `-m` | HTTP method — GET, POST, PUT, etc. (default: GET) |
| `--header`, `-H` | Request header as `Name: Value` (repeatable) |
| `--body`, `-b` | Request body |
| `--users`, `-c` | Virtual users (default: 1) |
| `--duration`, `-d` | Test duration in seconds |
| `--requests`, `-n` | Total request count (alternative to duration) |
| `--load-profile`, `-p` | CONSTANT, RAMP_UP, or SPIKE (default: CONSTANT) |
| `--ramp-up`, `-r` | Ramp-up period in seconds (required for RAMP_UP) |
| `--timeout`, `-t` | Per-request timeout in seconds (default: 30) |
| `--output`, `-o` | Write JSON report to this file |

### Examples

```bash
# 10 users hammering an endpoint for 60 seconds
java -jar target/stress-test-framework-1.0.0.jar \
  --url https://api.example.com/items \
  --users 10 \
  --duration 60

# Ramp up to 20 users over 10 seconds, run for 30
java -jar target/stress-test-framework-1.0.0.jar \
  --url https://api.example.com/items \
  --users 20 \
  --duration 30 \
  --load-profile RAMP_UP \
  --ramp-up 10

# POST with body, stop after 1000 requests, save JSON report
java -jar target/stress-test-framework-1.0.0.jar \
  --url https://api.example.com/items \
  --method POST \
  --header "Content-Type: application/json" \
  --body '{"name":"test"}' \
  --users 5 \
  --requests 1000 \
  --output report.json
```

## Programmatic API

```java
TestConfig config = new ScenarioBuilder()
        .url("https://api.example.com/items")
        .method(HttpMethod.POST)
        .body("{\"name\":\"test\"}")
        .header("Content-Type", "application/json")
        .virtualUsers(10)
        .duration(Duration.ofSeconds(60))
        .loadProfile(LoadProfile.RAMP_UP)
        .rampUpSeconds(15)
        .build();

TestResult result = new TestOrchestrator().run(config);
new ConsoleReporter().report(result);
new JsonReporter(Path.of("report.json")).report(result);
```

## Sample output

```
=================================================
  HTTP STRESS TEST RESULTS
=================================================
  Duration               60.12 s
  Total Requests         24,318
  Successful             24,315 (99.99%)
  Failed                 3 (0.01%)
  Requests/sec           404.47
-------------------------------------------------
  Latency (ms)
    Min                  8
    Avg                  24.61
    Median (p50)         23
    p90                  38
    p95                  47
    p99                  91
    Max                  612
-------------------------------------------------
  Status Codes
    200                  24,315
=================================================
```
