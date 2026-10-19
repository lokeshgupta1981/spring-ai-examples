package com.howtodoinjava.orderdesk.vectorstore;

import java.io.File;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.micrometer.observation.ObservationRegistry;

@Configuration
@ConditionalOnProperty(name = "spring.ai.vectorstore.type", havingValue = "simple")
public class SimpleVectorStoreConfig {

    // SimpleVectorStore keeps vectors in memory; we save them to this file
    public static final File STORE_FILE = new File("data/vector-store.json");

    @Bean
    SimpleVectorStore vectorStore(EmbeddingModel embeddingModel,
                                  ObjectProvider<ObservationRegistry> observationRegistry) {
        SimpleVectorStore store = SimpleVectorStore.builder(embeddingModel)
            // without a registry, searches don't show up in metrics and traces
            .observationRegistry(observationRegistry.getIfUnique(() -> ObservationRegistry.NOOP))
            .build();
        if (STORE_FILE.exists()) {
            store.load(STORE_FILE);
        }
        return store;
    }
}
