package com.howtodoinjava.orderdesk.evaluation;

import org.springframework.ai.chat.client.ChatClient;

/** The answer-writing step, isolated from order retrieval for evaluation. */
public class OrderAnswerWriter {
    private final ChatClient client;

    public OrderAnswerWriter(ChatClient client) {
        this.client = client;
    }

    public String answer(String instructions, String question, String context) {
        return client.prompt()
            .system(instructions)
            .user(u -> u.text("""
                Question: {question}
                Order data: {context}
                """)
                .param("question", question)
                .param("context", context))
            .call()
            .content();
    }
}
