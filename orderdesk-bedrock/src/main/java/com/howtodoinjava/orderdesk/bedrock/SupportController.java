package com.howtodoinjava.orderdesk.bedrock;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.metadata.ChatResponseMetadata;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SupportController {

    private final ChatClient hostedClient;
    private final ChatClient localClient;

    SupportController(@Qualifier("hostedClient") ChatClient hostedClient,
                      @Qualifier("localClient") ChatClient localClient) {
        this.hostedClient = hostedClient;
        this.localClient = localClient;
    }

    // model=hosted (default) or model=local
    @GetMapping("/ask")
    Reply ask(@RequestParam String question,
              @RequestParam(defaultValue = "hosted") String model) {
        ChatClient client = "local".equals(model) ? localClient : hostedClient;
        ChatResponse response = client.prompt().user(question).call().chatResponse();
        ChatResponseMetadata metadata = response.getMetadata();
        return new Reply(metadata.getModel(), response.getResult().getOutput().getText(),
            metadata.getUsage().getPromptTokens(), metadata.getUsage().getCompletionTokens());
    }
}
