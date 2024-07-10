package com.howtodoinjava.ai.demo;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
class EmbeddingController {

  private final EmbeddingModel embeddingModel;

  EmbeddingController(EmbeddingModel embeddingModel) {
    this.embeddingModel = embeddingModel;
  }

  @PostMapping("/embed")
  String embed(@RequestBody String message) {
    var embeddings = embeddingModel.embed(message);
    return "Size of the embedding vector: " + embeddings.size();
  }
}
