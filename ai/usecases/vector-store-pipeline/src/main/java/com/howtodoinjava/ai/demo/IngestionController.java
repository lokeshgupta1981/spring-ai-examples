package com.howtodoinjava.ai.demo;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IngestionController {

  IngestionService ingestionService;

  public IngestionController(IngestionService ingestionService){
    this.ingestionService = ingestionService;
  }

  @PostMapping("run-ingestion")
  public ResponseEntity<?> run(){
    ingestionService.ingest();
    return ResponseEntity.accepted().build();
  }
}
