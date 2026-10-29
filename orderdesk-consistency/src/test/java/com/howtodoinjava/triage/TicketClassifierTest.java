package com.howtodoinjava.triage;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import tools.jackson.databind.json.JsonMapper;
import static org.assertj.core.api.Assertions.*;
import static com.howtodoinjava.triage.TicketClassifier.*;

@SpringBootTest
class TicketClassifierTest {
    static final MockWebServer ollama = new MockWebServer();
    static final JsonMapper json = JsonMapper.builder().build();
    @Autowired TicketClassifier classifier;
    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.ai.ollama.base-url", () -> ollama.url("/").toString());
    }
    @AfterAll static void close() throws Exception { ollama.shutdown(); }
    static MockResponse reply(String content) {
        return new MockResponse().setHeader("Content-Type", "application/json")
            .setBody(json.writeValueAsString(Map.of("model", "qwen2.5:3b",
                "message", Map.of("role", "assistant", "content", content), "done", true)));
    }

    @Test
    void sendsSamplingOptionsAndNativeSchema() throws Exception {
        ollama.enqueue(reply("""
            {"category":"DELIVERY","priority":"NORMAL","summary":"The parcel is late."}
            """));
        var result = classifier.classify("My parcel is three days late.");
        var request = json.readTree(ollama.takeRequest().getBody().readUtf8());
        assertThat(request.at("/options/temperature").asDouble()).isZero(); // Sampling setting reaches Ollama.
        assertThat(request.at("/options/seed").asInt()).isEqualTo(42); // Fixed seed is transmitted.
        assertThat(request.at("/format/properties/category").isMissingNode()).isFalse(); // Native schema is present.
        assertThat(result.category()).isEqualTo(Category.DELIVERY); // Controlled JSON becomes our enum.
    }

    @Test
    void rejectsBlankSummary() throws Exception {
        ollama.enqueue(reply("""
            {"category":"DELIVERY","priority":"NORMAL","summary":" "}
            """));
        assertThatThrownBy(() -> classifier.classify("My parcel is late."))
            .isInstanceOf(IllegalStateException.class); // A valid JSON string can still be unusable.
        ollama.takeRequest();
    }

    @Test
    void identicalWrongDecisionsAreNotAccurate() {
        var wrong = new TicketTriage(Category.BILLING, Priority.NORMAL, "A late parcel.");
        var expected = new Repeatability.Decision(Category.DELIVERY, Priority.NORMAL);
        var report = Repeatability.measure(List.of(wrong, wrong, wrong), expected);
        assertThat(report.agreement()).isEqualTo(1.0); // Every decision is the same.
        assertThat(report.accuracy()).isZero(); // Every decision is wrong.
    }

    @Test
    void failedCallsRemainInTheDenominator() {
        var correct = new TicketTriage(Category.DELIVERY, Priority.NORMAL, "A late parcel.");
        var report = Repeatability.measure(Arrays.asList(correct, correct, null),
            new Repeatability.Decision(Category.DELIVERY, Priority.NORMAL));
        assertThat(report.failures()).isEqualTo(1);
        assertThat(report.accuracy()).isEqualTo(2.0 / 3); // Failure does not disappear from the score.
    }
}
