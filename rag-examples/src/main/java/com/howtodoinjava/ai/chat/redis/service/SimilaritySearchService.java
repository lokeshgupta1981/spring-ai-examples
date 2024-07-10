package com.howtodoinjava.ai.chat.redis.service;

import java.util.List;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;

//@Service
public class SimilaritySearchService {

  @Autowired
  private VectorStore vectorStore;

  public List<Document> searchData(String query) {
    return vectorStore.similaritySearch(query);
  }
}
