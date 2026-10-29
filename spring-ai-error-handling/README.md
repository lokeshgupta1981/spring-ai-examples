Source code for the article [Spring AI Error Handling with Retries, Timeouts and Fallbacks](https://howtodoinjava.com/spring-ai/spring-ai-error-handling/).

This standalone project uses Java 21 or later, Spring Boot 4.1.2, Spring AI 2.0.2 and MockWebServer 4.12.0. Tests were run with Java 25. It does not depend on OrderDesk or the repository's root Maven build.

## Run the tests

From this directory, run:

```shell
mvn test
```

Nine tests run without Ollama or an API key. They exercise the real auto-configured Spring AI client against a local MockWebServer. Cases cover normal success, HTTP 503 recovery, HTTP 401 non-retry, delayed response headers, retry exhaustion with an explicit fallback, structured-output correction and exhaustion, an HTTP 502 response after invalid output, and a malformed streaming chunk after partial output.

Expected result: 9 tests, 0 failures, 0 errors. Error and retry logging during these deliberately failing responses is expected.

The example's `spring.ai.retry.max-attempts=2` binds to two retries in the pinned Spring AI build. The HTTP request-count tests verify three total requests. Keep this assertion when upgrading dependencies instead of assuming how a property name counts attempts.

The timeout test delays response headers. A timeout during response-body decoding can be wrapped differently and is not claimed to follow the same retry path. The test configuration shortens read timeout to one second and initial retry delay to one millisecond; production example values are in application.properties.

## Run against Ollama

Start Ollama locally and download the model, then start the application:

```shell
ollama pull qwen2.5:3b
mvn spring-boot:run
```

In a second terminal:

```shell
curl --get http://localhost:8080/summary --data-urlencode "facts=Order ORD-1042 has status PACKED."
curl --get http://localhost:8080/summary/fallback --data-urlencode "facts=Order ORD-1042 has status PACKED."
curl --get http://localhost:8080/summary/structured --data-urlencode "facts=Order ORD-1042 has status PACKED."
curl -N --get http://localhost:8080/summary/stream --data-urlencode "facts=Order ORD-1042 has status PACKED."
```

On Windows PowerShell, use `curl.exe` if `curl` is an alias.

`SummaryService` demonstrates the normal call. `SummaryFacade` returns an explicit degraded result only for selected temporary failures. `StructuredSummaryService` allows one output correction and uses provider-native structured output. `StructuredSummaryController` maps failed generation to HTTP 502 without presenting invalid output as an order. `StreamingSummaryService` emits delta, complete or error events. The controllers expose all four services; blocking calls run on boundedElastic rather than a WebFlux event-loop thread.

The sample accepts supplied facts to keep error handling isolated. It is not an order lookup or an authorization implementation. Never use untrusted caller text as authoritative order data in a production feature.

Live Ollama generation was not run during draft verification. Controlled HTTP tests do not establish factual answer quality or model performance.
