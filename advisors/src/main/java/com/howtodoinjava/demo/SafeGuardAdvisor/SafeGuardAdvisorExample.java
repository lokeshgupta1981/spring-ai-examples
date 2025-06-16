package com.howtodoinjava.demo.SafeGuardAdvisor;

import java.util.List;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SafeGuardAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SafeGuardAdvisorExample implements CommandLineRunner {

  @Value("${spring.ai.openai.api-key}")
  String apiKey;

  public static void main(String[] args) {
    SpringApplication.run(SafeGuardAdvisorExample.class, args);
  }

  @Override
  public void run(String... args) {

    List<String> forbiddenWords = List.of("curse");
    SafeGuardAdvisor safeGuardAdvisor = new SafeGuardAdvisor(forbiddenWords);

    OpenAiApi openAiApi = OpenAiApi.builder().apiKey(apiKey).build();
    OpenAiChatModel chatModel = OpenAiChatModel.builder().openAiApi(openAiApi).build();

    ChatClient chatClient = ChatClient
      .builder(chatModel)
      .defaultAdvisors(safeGuardAdvisor)
      .defaultAdvisors(new SimpleLoggerAdvisor())
      .build();

    String responseContent = chatClient.prompt()
      .user("Can you spell curse?")
      .call()
      .content();

    System.out.printf("response: %s\n", responseContent);
  }
}
