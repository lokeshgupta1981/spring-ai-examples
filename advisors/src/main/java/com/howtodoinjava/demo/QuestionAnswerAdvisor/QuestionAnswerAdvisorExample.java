package com.howtodoinjava.demo.QuestionAnswerAdvisor;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class QuestionAnswerAdvisorExample implements CommandLineRunner {

  @Bean
  VectorStoreLoaderForRag vectorStoreLoaderForRag() {
    return new VectorStoreLoaderForRag();
  }

  @Value("${spring.ai.openai.api-key}")
  String apiKey;

  private final VectorStore vectorStore;

  public QuestionAnswerAdvisorExample(VectorStore vectorStore) {
    this.vectorStore = vectorStore;
  }

  public static void main(String[] args) {
    SpringApplication.run(QuestionAnswerAdvisorExample.class, args);
  }

  @Override
  public void run(String... args) {

    String authenticatedUserId = "test-123";

    QuestionAnswerAdvisor qaAdvisor = QuestionAnswerAdvisor.builder(vectorStore)
      .searchRequest(SearchRequest.builder().similarityThreshold(0.8d).topK(6).build())
      .build();

    OpenAiApi openAiApi = OpenAiApi.builder().apiKey(apiKey).build();
    OpenAiChatModel chatModel = OpenAiChatModel.builder().openAiApi(openAiApi).build();

    ChatClient chatClient = ChatClient
      .builder(chatModel)
      .defaultAdvisors(qaAdvisor)
      .defaultAdvisors(new SimpleLoggerAdvisor())
      .build();

    String responseContent = chatClient.prompt()
      .user("what are three cases of interest")
      .advisors(
        advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, authenticatedUserId))
      .call()
      .content();

    System.out.printf("response: %s\n", responseContent);
  }
}
