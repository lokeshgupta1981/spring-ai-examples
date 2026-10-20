package com.howtodoinjava.orderdesk.moderation;

import java.util.Arrays;
import java.util.List;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.ollama.api.OllamaChatOptions;
import org.springframework.stereotype.Component;

@Component
public class SafetyCheck {

    private final ChatClient guardClient;

    // A separate client that talks to the safety model, not to the chat model
    SafetyCheck(ChatClient.Builder builder) {
        this.guardClient = builder
            .defaultOptions(OllamaChatOptions.builder().model("llama-guard3:1b").temperature(0.0))
            .build();
    }

    // Llama Guard answers "safe", or "unsafe" and a line with codes such as "S1,S10"
    public Verdict check(String text) {
        String answer = guardClient.prompt().user(text).call().content().trim();
        if (answer.startsWith("safe")) {
            return new Verdict(true, List.of());
        }
        String[] lines = answer.split("\n");
        List<String> codes = lines.length > 1
            ? Arrays.stream(lines[1].split(",")).map(String::trim).toList()
            : List.of();
        return new Verdict(false, codes);
    }
}
