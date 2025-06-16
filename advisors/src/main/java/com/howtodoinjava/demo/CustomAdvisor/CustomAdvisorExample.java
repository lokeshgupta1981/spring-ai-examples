package com.howtodoinjava.demo.CustomAdvisor;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CustomAdvisorExample implements CommandLineRunner {

  @Value("${spring.ai.openai.api-key}")
  String apiKey;

  public static void main(String[] args) {
    SpringApplication.run(CustomAdvisorExample.class, args);
  }

  @Override
  public void run(String... args) {

    OpenAiApi openAiApi = OpenAiApi.builder().apiKey(apiKey).build();
    OpenAiChatModel chatModel = OpenAiChatModel.builder().openAiApi(openAiApi).build();

    ChatClient chatClient = ChatClient
      .builder(chatModel)
      .build();

    String responseContent = chatClient.prompt()
      .user("My name is: Lokesh. Say my name.")
      .advisors(new CustomAdvisor())
      .call()
      .content();

    System.out.printf("response: %s\n", responseContent);
  }
}

