package com.howtodoinjava.orderdesk.deepseek;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.deepseek.DeepSeekAssistantMessage;
import org.springframework.ai.deepseek.DeepSeekChatOptions;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReasoningController {

    private final ChatClient hostedClient;

    ReasoningController(@Qualifier("hostedClient") ChatClient hostedClient) {
        this.hostedClient = hostedClient;
    }

    // The model thinks first; the reasoning text comes back next to the answer
    @GetMapping("/ask/reason")
    Reasoned reason(@RequestParam String question) {
        ChatResponse response = hostedClient.prompt()
            .user(question)
            .options(DeepSeekChatOptions.builder().enableThinking())
            .call()
            .chatResponse();
        DeepSeekAssistantMessage message =
            (DeepSeekAssistantMessage) response.getResult().getOutput();
        return new Reasoned(message.getReasoningContent(), message.getText());
    }

    record Reasoned(String reasoning, String answer) {
    }
}
