package com.howtodoinjava.teamdocs;

import java.nio.charset.StandardCharsets;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import tools.jackson.databind.json.JsonMapper;

@SpringBootApplication
public class KnowledgeApplication {
    public static void main(String[] args) { SpringApplication.run(KnowledgeApplication.class, args); }

    @Bean
    VectorStore vectorStore(EmbeddingModel embeddingModel) {
        return SimpleVectorStore.builder(embeddingModel).build();
    }

    @Bean
    CommandLineRunner demo(KnowledgeIndex index, KnowledgeAssistant assistant,
            @Value("${app.team}") String team, @Value("${app.question}") String question) {
        return args -> {
            index.replace("engineering", "device-access.md", new ClassPathResource(
                "docs/device-access.md").getContentAsString(StandardCharsets.UTF_8));
            index.replace("finance", "invoice-access.md", new ClassPathResource(
                "docs/invoice-access.md").getContentAsString(StandardCharsets.UTF_8));
            var answer = assistant.ask(question, team);
            System.out.println(JsonMapper.builder().build().writerWithDefaultPrettyPrinter()
                .writeValueAsString(answer));
        };
    }
}
