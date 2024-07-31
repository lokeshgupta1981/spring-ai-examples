package com.howtodoinjava.ai.demo;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.evaluation.EvaluationRequest;
import org.springframework.ai.evaluation.EvaluationResponse;
import org.springframework.ai.evaluation.RelevancyEvaluator;
import org.springframework.ai.model.Content;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.chromadb.ChromaDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers(disabledWithoutDocker = true)
@Disabled
public class EvaluatorTests {

  @Container
  @ServiceConnection
  static ChromaDBContainer chroma = new ChromaDBContainer("ghcr.io/chroma-core/chroma:latest");

  @Autowired
  OpenAiChatModel chatModel;

  @Autowired
  QAService qaService;

  @Test
  void testEvaluation() {
    String question = "What is the grave digger card?";
    ChatResponse response = qaService.askQuestion(question);
    String responseContent = response.getResult().getOutput().getContent();
    var relevancyEvaluator = new RelevancyEvaluator(ChatClient.builder(chatModel));

    @SuppressWarnings("unchecked")
    List<Content> responseDocuments =
      (List<Content>) response.getMetadata().get(QuestionAnswerAdvisor.RETRIEVED_DOCUMENTS);

    EvaluationRequest evaluationRequest = new EvaluationRequest(question,
      responseDocuments, response.getResult().getOutput().getContent());

    EvaluationResponse evaluationResponse = relevancyEvaluator.evaluate(evaluationRequest);

    assertTrue(evaluationResponse.isPass(),
      "Response is not relevant to the asked question.\n" +
        "Question: " + question + "\n" +
        "Response: " + responseContent);
  }
}
