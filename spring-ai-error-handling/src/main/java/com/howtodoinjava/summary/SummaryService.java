package com.howtodoinjava.summary;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class SummaryService {
    private final ChatClient client;

    public SummaryService(ChatClient.Builder builder) {
        this.client = builder.build();
    }

    public String summarize(String facts) {
        return client.prompt()
            .system("Explain the supplied order facts in one sentence. Do not add facts.")
            .user(facts)
            .call()
            .content();
    }
}
