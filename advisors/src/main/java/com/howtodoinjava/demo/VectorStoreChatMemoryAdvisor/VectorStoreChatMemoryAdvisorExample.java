package com.howtodoinjava.demo.VectorStoreChatMemoryAdvisor;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.VectorStoreChatMemoryAdvisor;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class VectorStoreChatMemoryAdvisorExample implements CommandLineRunner {

  @Value("${spring.ai.openai.api-key}")
  String apiKey;

  private final VectorStore vectorStore;

  public VectorStoreChatMemoryAdvisorExample(VectorStore vectorStore) {
    this.vectorStore = vectorStore;
  }

  public static void main(String[] args) {
    SpringApplication.run(VectorStoreChatMemoryAdvisorExample.class, args);
  }

  @Override
  public void run(String... args) {

    VectorStoreChatMemoryAdvisor vectorStoreChatMemoryAdvisor
      = VectorStoreChatMemoryAdvisor.builder(vectorStore).build();

    OpenAiApi openAiApi = OpenAiApi.builder().apiKey(apiKey).build();
    OpenAiChatModel chatModel = OpenAiChatModel.builder().openAiApi(openAiApi).build();

    ChatClient chatClient = ChatClient
      .builder(chatModel)
      .defaultAdvisors(vectorStoreChatMemoryAdvisor)
      .defaultAdvisors(new SimpleLoggerAdvisor())
      .build();

    String responseContent = chatClient.prompt()
      .user("My name is: Lokesh. Say my name.")
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
