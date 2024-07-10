package com.howtodoinjava.ai.demo;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
class ClassificationController {

  private final TextClassifierService textClassifierService;

  ClassificationController(TextClassifierService textClassifierService) {
    this.textClassifierService = textClassifierService;
  }

  @PostMapping("/classify")
  String classify(@RequestBody String text) {
    return textClassifierService.classifyEmotion(text);
  }

  @PostMapping("/classify/structured-output")
  ClassificationType classifyStructured(@RequestBody String text) {
    return textClassifierService.classifyEmotionStructured(text);
  }

  @PostMapping("/classify/with-hints")
  String classifyWithHints(@RequestBody String text) {
    return textClassifierService.classifyEmotionWithHints(text);
  }

  @PostMapping("/classify/few-shots-prompt")
  String classifyFewShotsPrompt(@RequestBody String text) {
    return textClassifierService.classifyEmotionWithFewShotsPrompt(text);
  }

  @PostMapping("/classify/few-shots-history")
  String classifyFewShotsHistory(@RequestBody String text) {
    return textClassifierService.classifyEmotionWithFewShotsHistory(text);
  }


}