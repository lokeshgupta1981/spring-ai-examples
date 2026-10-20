package com.howtodoinjava.orderdesk.dmr;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatConfig {

    static final String SUPPORT = "You are OrderDesk, the support assistant of an online store. "
        + "Answer in at most three sentences.";

    // The model in Docker Model Runner, reached through the OpenAI API
    @Bean
    ChatClient runnerClient(OpenAiChatModel chatModel) {
        return ChatClient.builder(chatModel).defaultSystem(SUPPORT).build();
    }

    // The model in Ollama, as in the other OrderDesk articles
    @Bean
    ChatClient ollamaClient(OllamaChatModel chatModel) {
        return ChatClient.builder(chatModel).defaultSystem(SUPPORT).build();
    }
}
