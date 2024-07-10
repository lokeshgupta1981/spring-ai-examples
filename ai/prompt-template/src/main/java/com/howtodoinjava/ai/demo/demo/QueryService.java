package com.howtodoinjava.ai.demo.demo;

import java.util.Map;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
public class QueryService {

  private final ChatClient chatClient;

  @Value("classpath:/query-template.st")
  private Resource queryTemplate;

  public QueryService(ChatClient.Builder chatClientBuilder) {
    this.chatClient = chatClientBuilder.build();
  }

  public QueryResponse query(String subject) {

    return chatClient.prompt()
      .user(userSpec -> userSpec
        .text(queryTemplate)
        .param("subject", subject))
      .call()
      .entity(QueryResponse.class);
  }

  public QueryResponse query_v2(String subject) {

    PromptTemplate promptTemplate = new PromptTemplate(queryTemplate);
    Message message = promptTemplate.createMessage(Map.of("subject", subject));

    return chatClient.prompt()
      .user(message.getContent())
      .call()
      .entity(QueryResponse.class);
  }
}
