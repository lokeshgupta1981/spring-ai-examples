package com.howtodoinjava.ai.demo.demo;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class QueryController {

  private final ChatClient chatClient;
  private final QueryService queryService;

  @Value("classpath:/query-template.st")
  private Resource queryTemplate;

  public QueryController(ChatClient.Builder chatClientBuilder, QueryService queryService) {
    this.chatClient = chatClientBuilder.build();
    this.queryService = queryService;
  }

  @GetMapping("/explain")
  public QueryResponse query(@RequestParam("subject") String subject) {
    return queryService.query(subject);
  }
}
