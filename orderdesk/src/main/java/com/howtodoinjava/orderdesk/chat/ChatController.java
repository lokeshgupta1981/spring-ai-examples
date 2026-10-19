package com.howtodoinjava.orderdesk.chat;

import java.util.Map;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.ollama.api.OllamaChatOptions;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChatController {

    private final ChatClient supportClient;
    private final ChatClient summaryClient;

    ChatController(@Qualifier("supportClient") ChatClient supportClient,
                   @Qualifier("summaryClient") ChatClient summaryClient) {
        this.supportClient = supportClient;
        this.summaryClient = summaryClient;
    }

    // 1. The simplest call: a question in, a String out
    @GetMapping("/ask")
    String ask(@RequestParam String question) {
        return supportClient.prompt()
            .user(question)
            .call()
            .content();
    }

    // 2. A user message built from a template and a parameter
    @GetMapping("/greet")
    String greet(@RequestParam String customer) {
        return supportClient.prompt()
            .user(u -> u.text("Greet {name} and ask how you can help today.")
                        .param("name", customer))
            .call()
            .content();
    }

    // 3. A Java record instead of a String
    @GetMapping("/quote")
    ShippingQuote quote(@RequestParam String city, @RequestParam int orderValue) {
        return supportClient.prompt()
            .user(u -> u.text("Give the delivery quote for an order of INR {value} to {city}.")
                        .param("value", orderValue)
                        .param("city", city))
            .call()
            .entity(ShippingQuote.class);
    }

    // 4. The full ChatResponse, to read token usage
    @GetMapping("/ask-with-usage")
    Map<String, Object> askWithUsage(@RequestParam String question) {
        ChatResponse response = supportClient.prompt()
            .user(question)
            .call()
            .chatResponse();
        Usage usage = response.getMetadata().getUsage();
        return Map.of(
            "answer", response.getResult().getOutput().getText(),
            "promptTokens", usage.getPromptTokens(),
            "completionTokens", usage.getCompletionTokens());
    }

    // 5. Options for one request only
    @GetMapping("/ask-creative")
    String askCreative(@RequestParam String question) {
        return supportClient.prompt()
            .user(question)
            .options(OllamaChatOptions.builder().temperature(0.9))
            .call()
            .content();
    }

    // 6. A second ChatClient with its own system prompt
    @GetMapping("/summary")
    String summary(@RequestParam String message) {
        return summaryClient.prompt()
            .user(message)
            .call()
            .content();
    }
}
