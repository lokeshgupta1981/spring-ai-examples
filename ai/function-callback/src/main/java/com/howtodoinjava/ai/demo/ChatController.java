package com.howtodoinjava.ai.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChatController {

  private final ChatService chatService;

  ChatController(ChatService chatService) {
    this.chatService = chatService;
  }

  @GetMapping("/chat/function")
  String chat(@RequestParam String stockName) {
    return chatService.getQuoteByStockName(stockName);
  }
}
