package com.howtodoinjava.ai.demo;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
class ChatController {

  private final ChatService chatService;

  ChatController(ChatService chatService) {
    this.chatService = chatService;
  }

  @PostMapping("/chat/bean")
  PlayerInfo chatWithBeanOutput(@RequestBody Question question) {
    return chatService.chatWithBeanOutput(question);
  }

  @PostMapping("/chat/map")
  Map<String,Object> chatWithMapOutput(@RequestBody Question question) {
    return chatService.chatWithMapOutput(question);
  }

  @PostMapping("/chat/list")
  List<String> chatWithListOutput(@RequestBody Question question) {
    return chatService.chatWithListOutput(question);
  }

}
