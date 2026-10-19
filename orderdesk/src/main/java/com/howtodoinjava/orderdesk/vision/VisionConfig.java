package com.howtodoinjava.orderdesk.vision;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.ollama.api.OllamaChatOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VisionConfig {

    // qwen2.5:3b reads only text, so images go to a vision model
    @Bean
    ChatClient visionClient(ChatClient.Builder builder) {
        return builder
            .defaultSystem("You check photos of parcels for the OrderDesk support team.")
            .defaultOptions(OllamaChatOptions.builder().model("qwen2.5vl:3b").temperature(0.1))
            .build();
    }
}
