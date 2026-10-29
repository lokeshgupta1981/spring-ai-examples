package com.howtodoinjava.summary;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.retry.NonTransientAiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.client.ResourceAccessException;
import reactor.test.StepVerifier;
import tools.jackson.databind.json.JsonMapper;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, properties = {
    "spring.ai.retry.backoff.initial-interval=1ms",
    "spring.http.clients.read-timeout=1s"
})
class SummaryApplicationTest {
    static final MockWebServer ollama = new MockWebServer();
    static final String FACTS = "Order ORD-1042 has status PACKED.";
    @Autowired SummaryService summaries;
    @Autowired SummaryFacade fallback;
    @Autowired StructuredSummaryService structured;
    @Autowired StreamingSummaryService streaming;
    int requestsBeforeTest;

    @DynamicPropertySource
    static void modelUrl(DynamicPropertyRegistry properties) {
        properties.add("spring.ai.ollama.base-url", () -> ollama.url("/").toString());
    }

    @BeforeEach
    void recordRequestCount() {
        requestsBeforeTest = ollama.getRequestCount();
    }

    @AfterAll
    static void stopServer() throws IOException {
        ollama.shutdown();
    }

    static MockResponse modelReply(String content) {
        String body = JsonMapper.builder().build().writeValueAsString(Map.of(
            "model", "qwen2.5:3b",
            "message", Map.of("role", "assistant", "content", content),
            "done", true, "done_reason", "stop"));
        return new MockResponse().setHeader("Content-Type", "application/json").setBody(body);
    }

    @Test
    void returnsNormalSummary() {
        ollama.enqueue(modelReply("Your order is packed."));

        assertThat(summaries.summarize(FACTS)).isEqualTo("Your order is packed."); // The normal response reaches our service.
        assertThat(ollama.getRequestCount() - requestsBeforeTest).isEqualTo(1); // Success needs no retry.
    }

    @Test
    void invalidOutputReturnsBadGateway() {
        ollama.enqueue(modelReply("not JSON"));
        ollama.enqueue(modelReply("still not JSON"));
        var controller = new StructuredSummaryController(structured);
        var http = org.springframework.test.web.reactive.server.WebTestClient
            .bindToController(controller).build();

        http.get().uri(uri -> uri.path("/summary/structured")
                .queryParam("facts", FACTS).build())
            .exchange()
            .expectStatus().isEqualTo(502); // Invalid output never becomes a successful order response.
        assertThat(ollama.getRequestCount() - requestsBeforeTest).isEqualTo(2);
    }

    @Test
    void retriesTemporaryModelFailures() {
        ollama.enqueue(new MockResponse().setResponseCode(503));
        ollama.enqueue(new MockResponse().setResponseCode(503));
        ollama.enqueue(modelReply("Your order is packed."));

        String answer = summaries.summarize(FACTS);

        assertThat(answer).isEqualTo("Your order is packed."); // The third request succeeded.
        assertThat(ollama.getRequestCount() - requestsBeforeTest).isEqualTo(3); // Initial call plus two retries.
    }

    @Test
    void doesNotRetryInvalidCredentials() {
        ollama.enqueue(new MockResponse().setResponseCode(401));

        assertThatThrownBy(() -> summaries.summarize(FACTS))
            .isInstanceOf(NonTransientAiException.class); // Spring AI identifies a non-retryable failure.
        assertThat(ollama.getRequestCount() - requestsBeforeTest).isEqualTo(1); // No second HTTP request.
    }

    @Test
    void responseTimeoutStopsEachAttempt() {
        for (int i = 0; i < 3; i++) {
            ollama.enqueue(modelReply("Late answer").setHeadersDelay(3, TimeUnit.SECONDS));
        }

        assertThatThrownBy(() -> summaries.summarize(FACTS))
            .isInstanceOf(ResourceAccessException.class); // No response arrives within the one-second timeout.
        assertThat(ollama.getRequestCount() - requestsBeforeTest).isEqualTo(3); // Each attempt has its own timeout.
    }

    @Test
    void returnsFallbackAfterRetryExhaustion() {
        for (int i = 0; i < 3; i++) {
            ollama.enqueue(new MockResponse().setResponseCode(503));
        }

        var answer = fallback.summarize(FACTS);

        assertThat(answer.degraded()).isTrue(); // No model request succeeded.
        assertThat(answer.text()).contains("temporarily unavailable"); // Fixed availability message.
        assertThat(ollama.getRequestCount() - requestsBeforeTest).isEqualTo(3); // Fallback follows the retry limit.
    }

    @Test
    void correctsInvalidStructuredOutput() {
        ollama.enqueue(modelReply("not JSON"));
        ollama.enqueue(modelReply("""
            {"orderId":"ORD-1042","status":"PACKED"}
            """));

        var answer = structured.summarize(FACTS);

        assertThat(answer.status()).isEqualTo("PACKED"); // The corrected response was converted.
        assertThat(ollama.getRequestCount() - requestsBeforeTest).isEqualTo(2); // One generation plus one correction.
    }

    @Test
    void persistentInvalidOutputDoesNotBecomeASuccess() {
        ollama.enqueue(modelReply("not JSON"));
        ollama.enqueue(modelReply("still not JSON"));

        assertThatThrownBy(() -> structured.summarize(FACTS))
            .isInstanceOf(RuntimeException.class); // Final conversion still fails after correction is exhausted.
        assertThat(ollama.getRequestCount() - requestsBeforeTest).isEqualTo(2); // Correction remains bounded.
    }

    @Test
    void brokenStreamPreservesTextAndReportsFailure() {
        String chunk = """
            {"model":"qwen2.5:3b","message":{"role":"assistant","content":"Your order"},"done":false}
            """;
        ollama.enqueue(new MockResponse().setHeader("Content-Type", "application/x-ndjson")
            .setChunkedBody(chunk + "invalid JSON\n", chunk.length()));

        StepVerifier.create(streaming.summarize(FACTS))
            .assertNext(event -> {
                assertThat(event.type()).isEqualTo("delta"); // First provider chunk remains visible.
                assertThat(event.text()).isEqualTo("Your order");
            })
            .assertNext(event -> assertThat(event.type()).isEqualTo("error")) // Decoding failed after partial output.
            .expectComplete() // No application-level "complete" event was emitted.
            .verify(Duration.ofSeconds(5));
        assertThat(ollama.getRequestCount() - requestsBeforeTest).isEqualTo(1); // We never restart generation silently.
    }
}
