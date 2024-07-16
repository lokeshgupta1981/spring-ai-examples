package com.howtodoinjava.ai.demo;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IngestionController {

  EtlPipeline etlPipeline;

  public IngestionController(EtlPipeline etlPipeline){
    this.etlPipeline = etlPipeline;
  }

  @PostMapping("run-ingestion")
  public ResponseEntity<?> run(){
    etlPipeline.runIngestion();
    return ResponseEntity.accepted().build();
  }
}
