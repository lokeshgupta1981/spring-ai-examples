package com.howtodoinjava.ai.demo;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class ChatService {

  private final ChatClient chatClient;

  ChatService(ChatClient.Builder chatClientBuilder) {
    this.chatClient = chatClientBuilder.build();
  }

  String getQuoteByStockName(String stockName) {
    var userPromptTemplate = "Get the latest quote for {stockName}.";
    return chatClient.prompt()
      .user(userSpec -> userSpec
        .text(userPromptTemplate)
        .param("stockName", stockName)
      )
      .functions("quoteByStockNameFunction")
      .call()
      .content();
  }
}
