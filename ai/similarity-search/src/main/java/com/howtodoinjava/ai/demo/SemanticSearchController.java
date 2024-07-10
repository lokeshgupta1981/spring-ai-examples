package com.howtodoinjava.ai.demo;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
class SemanticSearchController {

  private final SemanticSearchService semanticSearchService;

  SemanticSearchController(SemanticSearchService semanticSearchService) {
    this.semanticSearchService = semanticSearchService;
  }

  @PostMapping("/semantic-search")
  List<LogRecord> semanticSearch(@RequestBody String query) {
    return semanticSearchService.semanticSearch(query);
  }

}
