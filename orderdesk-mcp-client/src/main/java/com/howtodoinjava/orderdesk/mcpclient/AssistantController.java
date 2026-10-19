package com.howtodoinjava.orderdesk.mcpclient;

import java.util.Arrays;
import java.util.List;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AssistantController {

    private final ChatClient chatClient;
    private final ToolCallbackProvider mcpTools;

    // ToolCallbackProvider holds the tools of every connected MCP server
    AssistantController(ChatClient.Builder builder, ToolCallbackProvider mcpTools) {
        this.mcpTools = mcpTools;
        this.chatClient = builder
            .defaultSystem("You help a store manager. Use the tools to look up orders.")
            .defaultToolCallbacks(mcpTools)
            .build();
    }

    @GetMapping("/tools")
    List<String> tools() {
        return Arrays.stream(mcpTools.getToolCallbacks())
            .map(t -> t.getToolDefinition().name())
            .toList();
    }

    @GetMapping("/ask")
    String ask(@RequestParam String question) {
        return chatClient.prompt()
            .user(question)
            .call()
            .content();
    }
}
