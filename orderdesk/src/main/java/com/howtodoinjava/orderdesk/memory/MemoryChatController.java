package com.howtodoinjava.orderdesk.memory;

import java.util.List;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MemoryChatController {

    private final ChatClient supportClient;
    private final ChatMemory chatMemory;
    private final MessageChatMemoryAdvisor memoryAdvisor;

    MemoryChatController(@Qualifier("supportClient") ChatClient supportClient,
                         ChatMemory chatMemory) {
        this.supportClient = supportClient;
        this.chatMemory = chatMemory;
        this.memoryAdvisor = MessageChatMemoryAdvisor.builder(chatMemory).build();
    }

    @PostMapping("/chat/{customerId}")
    String chat(@PathVariable String customerId, @RequestBody String message) {
        return supportClient.prompt()
            .user(message)
            .advisors(a -> a.advisors(memoryAdvisor)
                            .param(ChatMemory.CONVERSATION_ID, customerId))
            .call()
            .content();
    }

    // What the model will see from this conversation next time
    @GetMapping("/chat/{customerId}/history")
    List<String> history(@PathVariable String customerId) {
        return chatMemory.get(customerId).stream()
            .map(m -> m.getMessageType() + ": " + m.getText())
            .toList();
    }
}
