package com.howtodoinjava.orderdesk.agents;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WorkflowConfig {

    // A plain client without OrderDesk's support rules, for internal steps
    // such as classifying, checking and judging
    @Bean
    ChatClient workerClient(ChatClient.Builder builder) {
        return builder
            .defaultSystem("You are a precise assistant inside a customer support system.")
            .build();
    }
}
