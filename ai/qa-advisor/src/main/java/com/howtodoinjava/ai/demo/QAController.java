package com.howtodoinjava.ai.demo;

import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class QAController {

  private final QAService qaService;

  public QAController(QAService qaService) {
    this.qaService = qaService;
  }

  @PostMapping("/ask")
  public Answer ask(@RequestBody Question question) {
    ChatResponse chatResponse = qaService.askQuestion(question.question());
    return new Answer(chatResponse.getResult().getOutput().getContent());
  }
}
