package com.howtodoinjava.orderdesk;

import java.util.List;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatConfig {

    @Bean
    VectorStore vectorStore(EmbeddingModel embeddingModel) {
        SimpleVectorStore store = SimpleVectorStore.builder(embeddingModel).build();
        store.add(List.of(
            new Document("Items can be returned within 10 days of delivery."),
            new Document("A damaged item must be reported within 48 hours with a photo.")));
        return store;
    }

    @Bean
    ChatClient chatClient(ChatClient.Builder builder, ChatMemory chatMemory,
                          VectorStore vectorStore, OrderTools orderTools) {
        return builder
            .defaultSystem("You are OrderDesk, the support assistant of an online store. "
                + "Answer in at most three sentences.")
            .defaultAdvisors(
                MessageChatMemoryAdvisor.builder(chatMemory).build(),
                QuestionAnswerAdvisor.builder(vectorStore).build())
            .defaultTools(orderTools)
            .build();
    }
}
