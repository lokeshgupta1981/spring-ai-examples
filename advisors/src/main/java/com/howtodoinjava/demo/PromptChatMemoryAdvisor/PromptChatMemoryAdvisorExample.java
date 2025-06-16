package com.howtodoinjava.demo.PromptChatMemoryAdvisor;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
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
public class PromptChatMemoryAdvisorExample implements CommandLineRunner {

  @Value("${spring.ai.openai.api-key}")
  String apiKey;

  public static void main(String[] args) {
    SpringApplication.run(PromptChatMemoryAdvisorExample.class, args);
  }

  @Override
  public void run(String... args) {

    OpenAiApi openAiApi = OpenAiApi.builder().apiKey(apiKey).build();
    OpenAiChatModel chatModel = OpenAiChatModel.builder().openAiApi(openAiApi).build();

    ChatMemory chatMemory = MessageWindowChatMemory.builder().build();
    PromptChatMemoryAdvisor chatMemoryAdvisor = PromptChatMemoryAdvisor.builder(chatMemory).build();
    MessageChatMemoryAdvisor messageChatMemoryAdvisor = MessageChatMemoryAdvisor.builder(chatMemory).build();

    ChatClient chatClient = ChatClient.builder(chatModel)
      .defaultAdvisors(chatMemoryAdvisor)
      .defaultAdvisors(new SimpleLoggerAdvisor())
      .build();

    String responseContent = chatClient.prompt().user("My name is: Lokesh. Say my name.").call().content();

    System.out.printf("response: %s\n", responseContent);

    responseContent = chatClient.prompt().user("My blog name is: howtodoinjava. Say my name and blog name.").call().content();

    System.out.printf("response: %s\n", responseContent);

    responseContent = chatClient.prompt().user("What did i tell you about myself").call().content();

    System.out.printf("response: %s\n", responseContent);
  }
}
