package com.howtodoinjava.ai.demo;

import java.util.List;
import org.springframework.ai.openai.OpenAiAudioSpeechModel;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.openai.OpenAiImageModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder.Op;
import org.springframework.stereotype.Service;

@Service
public class SemanticSearchService {

  private final VectorStore vectorStore;

  SemanticSearchService(VectorStore vectorStore) {
    this.vectorStore = vectorStore;
  }

  List<LogRecord> semanticSearch(String query) {

    // With default parameters
    SearchRequest searchRequest = SearchRequest.query(query);

    // Customize parameters
    /* SearchRequest searchRequest = SearchRequest.defaults()
      .withTopK(10)
      .withFilterExpression(new FilterExpressionBuilder().eq("LEVEL", "INFO").build())
      .withSimilarityThreshold(0.5);*/

    return vectorStore.similaritySearch(SearchRequest.query(query))
      .stream()
      .map(document -> new LogRecord(document.getContent()))
      .toList();
  }
}
