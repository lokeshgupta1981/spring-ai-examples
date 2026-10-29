package com.howtodoinjava.orderdesk.integration;

import java.security.Principal;
import org.springframework.ai.chat.client.ChatClient;

public class OrderApiAssistant {
    private final ChatClient client;
    private final OrderApiTools tools;

    public OrderApiAssistant(ChatClient.Builder builder, String orderApiBaseUrl) {
        this.client = builder.build();
        this.tools = new OrderApiTools(OrderApiTools.orderApi(orderApiBaseUrl));
    }

    public String answer(String question, Principal principal) {
        return tools.ask(client, question, principal);
    }
}
