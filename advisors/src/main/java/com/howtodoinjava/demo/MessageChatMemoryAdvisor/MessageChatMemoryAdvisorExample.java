package com.howtodoinjava.demo.MessageChatMemoryAdvisor;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MessageChatMemoryAdvisorExample implements CommandLineRunner {

  @Value("${spring.ai.openai.api-key}")
  String apiKey;

  public static void main(String[] args) {
    SpringApplication.run(MessageChatMemoryAdvisorExample.class, args);
  }

  @Override
  public void run(String... args) {

    ChatMemory chatMemory = MessageWindowChatMemory.builder().build();
    MessageChatMemoryAdvisor messageChatMemoryAdvisor = MessageChatMemoryAdvisor.builder(chatMemory).build();

    OpenAiApi openAiApi = OpenAiApi.builder().apiKey(apiKey).build();
    OpenAiChatModel chatModel = OpenAiChatModel.builder().openAiApi(openAiApi).build();

    ChatClient chatClient = ChatClient
      .builder(chatModel)
      .defaultAdvisors(messageChatMemoryAdvisor)
      .defaultAdvisors(new SimpleLoggerAdvisor())
      .build();

    //TODO: Use a unique conversation ID to maintain context across multiple calls
    String conversationId = "test-123";

    String responseContent = chatClient.prompt()
      .user("My name is: Lokesh. Say my name.")
      .advisors(advisorSpec -> advisorSpec
        .param(ChatMemory.CONVERSATION_ID, conversationId))
      .call()
      .content();

    System.out.printf("response: %s\n", responseContent);

    responseContent = chatClient.prompt()
      .user("My blog name is: howtodoinjava. Say my name and blog name.")
      .call()
      .content();

    System.out.printf("response: %s\n", responseContent);

    responseContent = chatClient.prompt()
      .user("What did i tell you about myself")
      .call()
      .content();

    System.out.printf("response: %s\n", responseContent);
  }
}
