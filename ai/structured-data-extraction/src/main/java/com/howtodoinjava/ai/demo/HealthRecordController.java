package com.howtodoinjava.ai.demo;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthRecordController {

  private final HealthRecordService healthRecordService;

  HealthRecordController(HealthRecordService healthRecordService) {
    this.healthRecordService = healthRecordService;
  }

  @PostMapping("/extract-health-record")
  HealthRecord extract(@RequestBody String prompt) {
    return healthRecordService.extract(prompt);
  }
}
