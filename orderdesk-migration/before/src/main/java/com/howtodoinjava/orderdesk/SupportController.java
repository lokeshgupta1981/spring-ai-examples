package com.howtodoinjava.orderdesk;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Flux;

@RestController
public class SupportController {

    private final ChatClient chatClient;
    private final ObjectMapper objectMapper;

    SupportController(ChatClient chatClient, ObjectMapper objectMapper) {
        this.chatClient = chatClient;
        this.objectMapper = objectMapper;
    }

    // A question from one customer; the customer id keeps the conversations apart
    @GetMapping("/ask")
    String ask(@RequestParam String customer, @RequestParam String question) {
        return chatClient.prompt()
            .user(question)
            .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, customer))
            .options(OllamaOptions.builder().temperature(0.0).build())
            .call()
            .content();
    }

    // The same question as a stream of text pieces
    @GetMapping("/ask/stream")
    Flux<String> askStream(@RequestParam String customer, @RequestParam String question) {
        return chatClient.prompt()
            .user(question)
            .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, customer))
            .stream()
            .content();
    }

    // A ticket as a record, also written to the log as JSON
    @GetMapping("/triage")
    Ticket triage(@RequestParam String message) throws JsonProcessingException {
        Ticket ticket = chatClient.prompt()
            .user("Make a support ticket from this message. Category is RETURN, DELIVERY, "
                + "PAYMENT or OTHER: " + message)
            .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, "triage"))
            .call()
            .entity(Ticket.class);
        System.out.println("ticket " + objectMapper.writeValueAsString(ticket));
        return ticket;
    }
}
