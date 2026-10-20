package com.howtodoinjava.orderdesk.moderation;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ModerationController {

    private final SafetyCheck safetyCheck;
    private final ChatClient chatClient;

    ModerationController(SafetyCheck safetyCheck, ModerationAdvisor moderationAdvisor,
                         ChatClient.Builder builder) {
        this.safetyCheck = safetyCheck;
        this.chatClient = builder
            .defaultSystem("You are OrderDesk, the support assistant of an online store. "
                + "Answer in at most three sentences.")
            .defaultAdvisors(moderationAdvisor)
            .build();
    }

    // Only the safety check, without the chat model
    @GetMapping("/moderation/check")
    Verdict check(@RequestParam String text) {
        return safetyCheck.check(text);
    }

    // A support question that passes the safety check first
    @GetMapping("/moderation/ask")
    String ask(@RequestParam String question) {
        return chatClient.prompt().user(question).call().content();
    }
}
