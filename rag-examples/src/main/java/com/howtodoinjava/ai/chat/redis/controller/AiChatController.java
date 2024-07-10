package com.howtodoinjava.ai.chat.redis.controller;

import com.howtodoinjava.ai.chat.redis.service.DataIngestionService;
import com.howtodoinjava.ai.chat.redis.service.SimilaritySearchService;
import java.util.List;
import java.util.Map;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

//@RestController
public class AiChatController {

  @Autowired
  DataIngestionService dataIngestionService;

  @Autowired
  SimilaritySearchService similaritySearchService;

  @Autowired
  private ChatModel chatModel;

  private final String INPUT_PROMPT = """
        Answer the query strictly referring the provided context:
        {context}
        Query:
        {query}
        In case you don't have any answer from the context provided, just say:
        I'm sorry I don't have the information you are looking for.
    """;

  @GetMapping("/chat")
  public ResponseEntity<?> chat(@RequestParam(name = "query") String query) {

    List<Document> documents = similaritySearchService.searchData(query);

    if (documents.size() > 0) {
      PromptTemplate promptTemplate = new PromptTemplate(INPUT_PROMPT);
      Prompt prompt = promptTemplate.create(Map.of("query", query, "context", documents));
      String response = chatModel.call(prompt).getResult().getOutput().getContent();
      return ResponseEntity.ok(response);
    }

    return ResponseEntity.status(201)
      .body("I'm sorry I don't have the information you are looking for");
  }

  @PostMapping("/store-embeddings")
  public ResponseEntity<?> ingestData() {
    try {
      //dataIngestionService.load();
      return ResponseEntity.ok("Success");
    } catch (Exception e) {
      return ResponseEntity.status(500).body(e.getLocalizedMessage());
    }
  }
}
