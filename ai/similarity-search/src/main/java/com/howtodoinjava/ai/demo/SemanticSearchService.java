package com.howtodoinjava.ai.demo;

import java.util.List;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

@Service
public class SemanticSearchService {

  private final VectorStore vectorStore;

  SemanticSearchService(VectorStore vectorStore) {
    this.vectorStore = vectorStore;
  }

  List<LogRecord> semanticSearch(String query) {
    return vectorStore.similaritySearch(SearchRequest.query(query))
      .stream()
      .map(document -> new LogRecord(document.getContent()))
      .toList();
  }
}
