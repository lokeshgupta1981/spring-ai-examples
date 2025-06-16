package com.howtodoinjava.ai.demo.web;


import com.howtodoinjava.ai.demo.web.model.Answer;
import com.howtodoinjava.ai.demo.web.model.Question;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chat")
public class ChatController {

  private final ChatClient chatClient;

  public ChatController(ChatClient.Builder chatClientBuilder, VectorStore vectorStore) {

    var qaAdvisor = QuestionAnswerAdvisor.builder(vectorStore)
      .searchRequest(SearchRequest.builder().similarityThreshold(0.8d).topK(6).build())
      .build();

    this.chatClient = chatClientBuilder
      .defaultAdvisors(
        new SimpleLoggerAdvisor(),
        qaAdvisor,
        MessageChatMemoryAdvisor.builder(MessageWindowChatMemory.builder().build()).build())
      .build();
  }

  @PostMapping
  public Answer chat(@RequestBody Question question, Authentication user) {
    return chatClient.prompt()
      .user(question.question())
      .advisors(
        advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, user.getPrincipal()))
      .call()
      .entity(Answer.class);
  }
}
