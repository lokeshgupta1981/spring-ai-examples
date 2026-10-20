package com.howtodoinjava.orderdesk.anthropic;

import org.springframework.ai.anthropic.AnthropicChatOptions;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ThinkingController {

    private final ChatClient hostedClient;

    ThinkingController(@Qualifier("hostedClient") ChatClient hostedClient) {
        this.hostedClient = hostedClient;
    }

    // Extended thinking: Claude may use up to 1,024 tokens to reason before it answers
    @GetMapping("/ask/think")
    Thought think(@RequestParam String question) {
        ChatResponse response = hostedClient.prompt()
            .user(question)
            .options(AnthropicChatOptions.builder()
                .thinkingEnabled(1024)
                .maxTokens(2048)
                .temperature(1.0))
            .call()
            .chatResponse();

        // Each thinking block comes back as its own generation, marked with a "signature"
        StringBuilder thinking = new StringBuilder();
        StringBuilder answer = new StringBuilder();
        for (Generation generation : response.getResults()) {
            AssistantMessage message = generation.getOutput();
            if (message.getMetadata().containsKey("signature")) {
                thinking.append(message.getText());
            } else if (message.getText() != null) {
                answer.append(message.getText());
            }
        }
        return new Thought(thinking.toString(), answer.toString());
    }

    record Thought(String thinking, String answer) {
    }
}
