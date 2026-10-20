package com.howtodoinjava.orderdesk.gemini;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.google.genai.GoogleGenAiChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatConfig {

    static final String SUPPORT = "You are OrderDesk, the support assistant of an online store. "
        + "Answer in at most three sentences.";

    // The hosted model: for questions that need a stronger model
    @Bean
    ChatClient hostedClient(GoogleGenAiChatModel chatModel) {
        return ChatClient.builder(chatModel).defaultSystem(SUPPORT).build();
    }

    // The local model in Ollama: free, for simple questions
    @Bean
    ChatClient localClient(OllamaChatModel chatModel) {
        return ChatClient.builder(chatModel).defaultSystem(SUPPORT).build();
    }
}
