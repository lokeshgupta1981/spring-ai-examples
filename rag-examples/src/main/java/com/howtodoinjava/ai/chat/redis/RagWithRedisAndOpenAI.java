package com.howtodoinjava.ai.chat.redis;

import java.util.List;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.openai.OpenAiEmbeddingOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class RagWithRedisAndOpenAI implements CommandLineRunner {

  public static void main(String[] args) {
    new SpringApplicationBuilder(RagWithRedisAndOpenAI.class)
       .run(args);
  }

  @Autowired
  OpenAiEmbeddingModel embeddingModel;
  @Override
  public void run(String... args) throws Exception {
    EmbeddingResponse embeddingResponse = embeddingModel.call(
      new EmbeddingRequest(List.of("Hello World", "World is big and salvation is near"),
        OpenAiEmbeddingOptions.builder()
          .withModel("text-embedding-3-small")
          .build()));
  }
}
