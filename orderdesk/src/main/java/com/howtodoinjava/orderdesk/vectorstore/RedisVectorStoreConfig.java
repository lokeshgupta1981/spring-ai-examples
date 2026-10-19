package com.howtodoinjava.orderdesk.vectorstore;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.redis.RedisVectorStore;
import org.springframework.ai.vectorstore.redis.RedisVectorStore.MetadataField;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import redis.clients.jedis.RedisClient;

@Configuration
@Profile("redis")
public class RedisVectorStoreConfig {

    @Bean
    RedisVectorStore vectorStore(EmbeddingModel embeddingModel,
                                 @Value("${spring.data.redis.host}") String host,
                                 @Value("${spring.data.redis.port}") int port) {
        return RedisVectorStore.builder(RedisClient.create(host, port), embeddingModel)
            .indexName("orderdesk-policies")
            .prefix("policy:")
            // Redis can filter only on metadata fields declared in the index
            .metadataFields(
                MetadataField.tag("topic"),
                MetadataField.tag("source"))
            .initializeSchema(true)
            .build();
    }
}
