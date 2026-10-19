package com.howtodoinjava.orderdesk.chat;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatClientConfig {

    static final String SUPPORT_RULES = """
        You are OrderDesk, the support assistant of an online store.
        Answer in at most three sentences.
        Delivery: Pune, Mumbai and Delhi in 2 days; every other city in India in 5 days.
        Delivery is free above INR 499; below that it costs INR 49.
        We do not deliver outside India.
        """;

    @Bean
    ChatClient supportClient(ChatClient.Builder builder) {
        return builder
            .defaultSystem(SUPPORT_RULES)
            .build();
    }

    @Bean
    ChatClient summaryClient(ChatClient.Builder builder) {
        return builder
            .defaultSystem("Summarize the customer's message in one line of at most 12 words.")
            .build();
    }
}
