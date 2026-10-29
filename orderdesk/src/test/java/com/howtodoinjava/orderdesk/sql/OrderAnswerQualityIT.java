package com.howtodoinjava.orderdesk.sql;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.evaluation.FactCheckingEvaluator;
import org.springframework.ai.document.Document;
import org.springframework.ai.evaluation.EvaluationRequest;
import org.springframework.ai.ollama.api.OllamaChatOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
    "spring.datasource.url=jdbc:hsqldb:mem:order-answer-eval",
    "spring.ai.mcp.server.enabled=false"})
@EnabledIfEnvironmentVariable(named = "RUN_LLM_EVAL", matches = "true")
class OrderAnswerQualityIT {

    private static final String QUESTION = "How many orders went to Pune?";
    private static final String JUDGE_MODEL = "bespoke-minicheck";
    private static final List<Document> CONTEXT = List.of(new Document(
        "The orders table contains exactly three orders with city Pune."));

    @Autowired OrderQueryController controller;
    @Autowired ChatClient.Builder judgeBuilder;

    private FactCheckingEvaluator evaluator() {
        ChatClient.Builder judge = judgeBuilder.clone()
            .defaultSystem("Judge only whether the claim agrees with the document. Reply YES or NO only.")
            .defaultOptions(OllamaChatOptions.builder().model(JUDGE_MODEL).temperature(0.0));
        return FactCheckingEvaluator.builder(judge).build();
    }

    @Test
    void oneLiveAnswerReturnsTheExpectedCount() {
        var result = controller.ask(QUESTION);

        assertThat(result.rows()).hasSize(1);
        assertThat(((Number) result.rows().getFirst().values().iterator().next()).intValue())
            .isEqualTo(3);
        assertThat(result.answer()).isNotBlank();
    }

    @Test
    void judgeMatchesLabeledAnswers() {
        record Case(String answer, boolean supported) { }
        List<Case> cases = List.of(
            new Case("Three orders went to Pune.", true),
            new Case("Pune received 3 orders.", true),
            new Case("Two orders went to Pune.", false),
            new Case("Three orders did not go to Pune.", false),
            new Case("The store sells jackets.", false));

        FactCheckingEvaluator judge = evaluator();
        List<String> mismatches = new ArrayList<>();
        for (Case example : cases) {
            boolean actual = judge.evaluate(new EvaluationRequest(
                QUESTION, CONTEXT, example.answer())).isPass();
            System.out.printf("Answer=%s | expected=%s | observed=%s%n",
                example.answer(), example.supported(), actual);
            if (actual != example.supported()) {
                mismatches.add(example.answer() + " expected=" + example.supported()
                    + " observed=" + actual);
            }
        }
        assertThat(mismatches).isEmpty();
    }

    @Test
    void repeatedAnswersMeetTheSampleThreshold() {
        FactCheckingEvaluator judge = evaluator();
        List<String> failures = new ArrayList<>();
        int attempts = 10;

        for (int i = 1; i <= attempts; i++) {
            var result = controller.ask(QUESTION);
            boolean countIsThree = result.rows().size() == 1
                && result.rows().getFirst().values().iterator().next() instanceof Number count
                && count.intValue() == 3;
            boolean supported = countIsThree && judge.evaluate(new EvaluationRequest(
                QUESTION, CONTEXT, result.answer())).isPass();
            if (!supported) {
                failures.add("Attempt " + i + ": SQL=" + result.sql()
                    + ", rows=" + result.rows() + ", answer=" + result.answer());
            }
        }

        failures.forEach(System.out::println);
        System.out.printf("Accepted %d of %d attempts%n", attempts - failures.size(), attempts);
        assertThat(attempts - failures.size())
            .withFailMessage("Failed samples: %s", failures)
            .isGreaterThanOrEqualTo(8);
    }
}
