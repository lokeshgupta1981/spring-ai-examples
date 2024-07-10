package com.howtodoinjava.ai.demo;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

@Service
public class QAService {

  private final ChatClient chatClient;

  public QAService(ChatClient.Builder chatClientBuilder, VectorStore vectorStore) {
    this.chatClient = chatClientBuilder
        .defaultAdvisors(new QuestionAnswerAdvisor(vectorStore, SearchRequest.defaults()))
        .build();
  }
  
  public ChatResponse askQuestion(String question) {
    return chatClient
        .prompt()
        .user(question)
        .call()
        .chatResponse();
  }
  
}
