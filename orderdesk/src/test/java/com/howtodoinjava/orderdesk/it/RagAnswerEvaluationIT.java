package com.howtodoinjava.orderdesk.it;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.evaluation.FactCheckingEvaluator;
import org.springframework.ai.chat.evaluation.RelevancyEvaluator;
import org.springframework.ai.document.Document;
import org.springframework.ai.evaluation.EvaluationRequest;
import org.springframework.ai.evaluation.EvaluationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import com.howtodoinjava.orderdesk.etl.PolicyIngestion;

// Uses the Ollama on this machine (qwen2.5:3b) both to answer and to judge.
// Run with: mvn test -Dtest=RagAnswerEvaluationIT
@SpringBootTest(properties = {
    "spring.datasource.url=jdbc:hsqldb:mem:eval",
    "spring.ai.mcp.server.enabled=false"})
class RagAnswerEvaluationIT {

    static final String QUESTION = "How many days do I have to return a jacket?";

    @Autowired @Qualifier("supportClient") ChatClient supportClient;
    @Autowired QuestionAnswerAdvisor questionAnswerAdvisor;
    @Autowired PolicyIngestion policyIngestion;
    @Autowired ChatClient.Builder judgeBuilder;

    // The evaluators compare the reply with "yes" exactly, so the judge must
    // answer with one word; a small model adds an explanation otherwise
    ChatClient.Builder judge() {
        return judgeBuilder.clone()
            .defaultSystem("Reply with one word only: yes or no. No explanation.")
            .defaultOptions(org.springframework.ai.ollama.api.OllamaChatOptions.builder()
                .temperature(0.0));
    }

    @Test
    @Disabled("qwen2.5:3b answers NO even for a correct answer; use a larger judge model")
    void ragAnswerIsRelevantToTheRetrievedPolicy() throws Exception {
        policyIngestion.ingest();
        ChatClientResponse response = supportClient.prompt()
            .user(QUESTION)
            .advisors(questionAnswerAdvisor)
            .call()
            .chatClientResponse();
        String answer = response.chatResponse().getResult().getOutput().getText();
        @SuppressWarnings("unchecked")
        List<Document> documents = (List<Document>) response.context()
            .get(QuestionAnswerAdvisor.RETRIEVED_DOCUMENTS);

        EvaluationResponse relevancy = new RelevancyEvaluator(judge())
            .evaluate(new EvaluationRequest(QUESTION, documents, answer));

        System.out.println("Answer: " + answer);
        System.out.println("Relevant: " + relevancy.isPass());
        assertThat(relevancy.isPass()).isTrue();
    }

    @Test
    void factCheckCatchesAnInventedPolicy() throws Exception {
        List<Document> policy = List.of(new Document(
            "Return window\nYou can return most items within 10 days of delivery."));
        FactCheckingEvaluator factChecker = FactCheckingEvaluator.builder(judge()).build();

        EvaluationResponse right = factChecker.evaluate(new EvaluationRequest(
            QUESTION, policy, "You have 10 days from delivery to return the jacket."));
        EvaluationResponse wrong = factChecker.evaluate(new EvaluationRequest(
            QUESTION, policy, "You have 30 days from delivery to return the jacket."));

        System.out.println("10 days supported: " + right.isPass());
        System.out.println("30 days supported: " + wrong.isPass());
        assertThat(right.isPass()).isTrue();
        assertThat(wrong.isPass()).isFalse();
    }
}
