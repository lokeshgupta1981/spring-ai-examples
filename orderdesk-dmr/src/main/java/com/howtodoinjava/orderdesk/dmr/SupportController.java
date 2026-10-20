package com.howtodoinjava.orderdesk.dmr;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.metadata.ChatResponseMetadata;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SupportController {

    private final ChatClient runnerClient;
    private final ChatClient ollamaClient;

    SupportController(@Qualifier("runnerClient") ChatClient runnerClient,
                      @Qualifier("ollamaClient") ChatClient ollamaClient) {
        this.runnerClient = runnerClient;
        this.ollamaClient = ollamaClient;
    }

    // model=runner (default) or model=ollama
    @GetMapping("/ask")
    Reply ask(@RequestParam String question,
              @RequestParam(defaultValue = "runner") String model) {
        ChatClient client = "ollama".equals(model) ? ollamaClient : runnerClient;
        ChatResponse response = client.prompt().user(question).call().chatResponse();
        ChatResponseMetadata metadata = response.getMetadata();
        return new Reply(metadata.getModel(), response.getResult().getOutput().getText(),
            metadata.getUsage().getPromptTokens(), metadata.getUsage().getCompletionTokens());
    }
}
