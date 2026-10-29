package com.howtodoinjava.summary;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.StructuredOutputValidationAdvisor;
import org.springframework.stereotype.Service;

@Service
public class StructuredSummaryService {
    public record OrderSummary(String orderId, String status) {}
    private final ChatClient client;

    public StructuredSummaryService(ChatClient.Builder builder) {
        this.client = builder.build();
    }

    public OrderSummary summarize(String facts) {
        return client.prompt()
            .system("Copy the order ID and status exactly from the supplied facts.")
            .user(facts)
            .advisors(StructuredOutputValidationAdvisor.builder()
                .outputType(OrderSummary.class)
                .maxRepeatAttempts(1)
                .build())
            .call()
            .entity(OrderSummary.class, spec -> spec.useProviderStructuredOutput());
    }
}
