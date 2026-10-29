package com.howtodoinjava.orderdesk.sql;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/** Runs only when a local model and the OrderDesk application dependencies are available. */
@SpringBootTest(properties = "spring.ai.mcp.server.enabled=false")
@EnabledIfEnvironmentVariable(named = "RUN_LLM_EVAL", matches = "true")
class OrderAnswerQualityIT {

    @Autowired
    OrderQueryController controller;

    @Test
    void mostAnswersPassTheOrderCountRubric() {
        int passes = 0;
        int attempts = 10;

        for (int i = 0; i < attempts; i++) {
            var result = controller.ask("How many orders went to Pune?");
            boolean correctRow = result.rows().size() == 1
                && ((Number) result.rows().getFirst().values().iterator().next()).intValue() == 3;
            String answer = result.answer();
            boolean answerMentionsCount = answer.matches("(?is).*\\b(3|three)\\b(?![,\\d]).*");
            boolean answerNamesTask = answer.matches("(?is).*\\borders?\\b.*")
                && answer.matches("(?is).*\\bPune\\b.*");
            if (correctRow && answerMentionsCount && answerNamesTask) {
                passes++;
            }
        }

        assertThat(passes).as("acceptable answers out of %s", attempts).isGreaterThanOrEqualTo(8);
    }
}
