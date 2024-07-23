package com.howtodoinjava.ai.demo;

import java.util.List;
import java.util.Map;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.stereotype.Service;

@Service
public class ChatService {

  private final ChatClient chatClient;
  private final ChatModel chatModel;


  ChatService(ChatClient.Builder chatClientBuilder,
    ChatModel chatModel) {
    this.chatClient = chatClientBuilder.build();
    this.chatModel = chatModel;
  }

  String getPriceByStockName(String stockName) {
    var userPromptTemplate = "Get the latest price for {stockName}.";

    PromptTemplate promptTemplate = new PromptTemplate(userPromptTemplate);
    Message message = promptTemplate.createMessage(Map.of("stockName", stockName));

    ChatResponse response = chatModel.call(
      new Prompt(List.of(message), OpenAiChatOptions.builder()
        .withFunction("priceByStockNameFunction").build()));

    return response.getResult().getOutput().getContent();

    /*return chatClient.prompt()
      .user(userSpec -> userSpec
        .text(userPromptTemplate)
        .param("stockName", stockName)
      )
      .functions("priceByStockNameFunction")
      .call()
      .content();*/
  }
}
