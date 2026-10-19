package com.howtodoinjava.orderdesk.vectorstore;

import java.io.File;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "spring.ai.vectorstore.type", havingValue = "simple")
public class SimpleVectorStoreConfig {

    // SimpleVectorStore keeps vectors in memory; we save them to this file
    public static final File STORE_FILE = new File("data/vector-store.json");

    @Bean
    SimpleVectorStore vectorStore(EmbeddingModel embeddingModel) {
        SimpleVectorStore store = SimpleVectorStore.builder(embeddingModel).build();
        if (STORE_FILE.exists()) {
            store.load(STORE_FILE);
        }
        return store;
    }
}
