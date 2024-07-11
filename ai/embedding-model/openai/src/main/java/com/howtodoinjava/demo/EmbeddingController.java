package com.howtodoinjava.demo;

import java.util.Arrays;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.openai.OpenAiEmbeddingOptions;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
class EmbeddingController {

  private final EmbeddingModel embeddingModel;

  EmbeddingController(EmbeddingModel embeddingModel) {
    this.embeddingModel = embeddingModel;
  }

  @PostMapping("/embed")
  Response embed(@RequestBody String message) {

    var embeddings = embeddingModel.embed(message);
    return new Response(embeddings.size(), embeddings);
  }

  @PostMapping("/embed/openai-options")
  String embedWithOpenAiOptions(@RequestBody String message) {

    if (message == null || message.length() == 0) {
      return "Empty content for embedding";
    }

    var embeddings = embeddingModel.call(
        new EmbeddingRequest(List.of(message), OpenAiEmbeddingOptions.builder()
          .withModel("text-embedding-3-small")
          .withUser("jon.snow")
          .build()))
      .getResult().getOutput();
    return "Size of the embedding vector: " + embeddings.size();
  }

  /*@PostMapping("/embed/ollama-options")
  String embedWithOllamaOptions(@RequestBody String message) {

    if (message == null || message.length() == 0) {
      return "Empty content for embedding";
    }

    var embeddings = embeddingModel.call(
        new EmbeddingRequest(List.of(message), OllamaOptions.create()
          .withModel("mistral")))
      .getResult().getOutput();
    return "Size of the embedding vector: " + embeddings.size();
  }*/

  /*@PostMapping("/embed/ollama-options")
  String embedWithMistralAiOptions(@RequestBody String message) {

    if (message == null || message.length() == 0) {
      return "Empty content for embedding";
    }

    var embeddings = embeddingModel.call(
        new EmbeddingRequest(List.of(message), MistralAiEmbeddingOptions.builder()
              .withModel("mistral-embed").build()))
          .getResult().getOutput();
    return "Size of the embedding vector: " + embeddings.size();
  }*/

}

record Response(int dimension, List<Double> vector){
}
