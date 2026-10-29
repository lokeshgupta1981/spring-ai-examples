package com.howtodoinjava.summary;

import java.time.Duration;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class StreamingSummaryService {
    public record SummaryEvent(String type, String text) {}
    private final ChatClient client;
    private final Duration idleTimeout;

    public StreamingSummaryService(ChatClient.Builder builder,
            @Value("${app.summary.stream-idle-timeout:20s}") Duration idleTimeout) {
        this.client = builder.build();
        this.idleTimeout = idleTimeout;
    }

    public Flux<SummaryEvent> summarize(String facts) {
        return Flux.defer(() -> client.prompt()
                .system("Explain only the supplied order facts.")
                .user(facts).stream().content())
            .timeout(idleTimeout)
            .map(text -> new SummaryEvent("delta", text))
            .concatWithValues(new SummaryEvent("complete", ""))
            .onErrorResume(failure -> Flux.just(new SummaryEvent("error",
                "The summary stopped before completion. Please try again.")));
    }
}
