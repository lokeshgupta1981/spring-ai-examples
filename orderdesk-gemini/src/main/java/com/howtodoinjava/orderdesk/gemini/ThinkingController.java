package com.howtodoinjava.orderdesk.gemini;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.google.genai.GoogleGenAiChatOptions;
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

    // Gemini 2.5 Flash thinks by default; a budget of 0 turns thinking off for this call
    @GetMapping("/ask/fast")
    String fast(@RequestParam String question) {
        return hostedClient.prompt()
            .user(question)
            .options(GoogleGenAiChatOptions.builder().thinkingBudget(0))
            .call()
            .content();
    }
}
