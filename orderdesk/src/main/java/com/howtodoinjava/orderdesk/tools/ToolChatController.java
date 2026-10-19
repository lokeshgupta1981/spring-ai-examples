package com.howtodoinjava.orderdesk.tools;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ToolChatController {

    private final ChatClient supportClient;
    private final OrderTools orderTools;

    ToolChatController(@Qualifier("supportClient") ChatClient supportClient,
                       OrderTools orderTools) {
        this.supportClient = supportClient;
        this.orderTools = orderTools;
    }

    @GetMapping("/tools/ask")
    String ask(@RequestParam String question) {
        return supportClient.prompt()
            .user(question)
            .tools(orderTools)
            .call()
            .content();
    }
}
