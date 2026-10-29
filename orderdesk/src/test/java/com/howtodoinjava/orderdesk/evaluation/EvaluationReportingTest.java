package com.howtodoinjava.orderdesk.evaluation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.evaluation.FactCheckingEvaluator;
import org.springframework.ai.chat.evaluation.RelevancyEvaluator;
import org.springframework.ai.evaluation.EvaluationResponse;
import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class EvaluationReportingTest {
    @BeforeEach
    void setup() {
        OrderAnswerEvaluationIT.writer = mock(OrderAnswerWriter.class);
        OrderAnswerEvaluationIT.relevance = mock(RelevancyEvaluator.class);
        OrderAnswerEvaluationIT.facts = mock(FactCheckingEvaluator.class);
    }

    @Test
    void factualFailurePreservesAnswerAndRelevanceVerdict() {
        when(OrderAnswerEvaluationIT.writer.answer(anyString(), anyString(), anyString()))
            .thenReturn("Three orders went to Pune.");
        when(OrderAnswerEvaluationIT.relevance.evaluate(any()))
            .thenReturn(new EvaluationResponse(true, "", Map.of()));
        when(OrderAnswerEvaluationIT.facts.evaluate(any()))
            .thenThrow(new IllegalStateException("Judge unavailable"));
        var sample = OrderAnswerEvaluationIT.evaluate("baseline", "prompt",
            OrderAnswerEvaluationIT.CASES.getFirst(), 1);
        assertThat(sample.answer()).isEqualTo("Three orders went to Pune.");
        assertThat(sample.relevant()).isTrue();
        assertThat(sample.supported()).isNull();
        assertThat(sample.error()).contains("factual evaluation", "Judge unavailable");
        assertThat(sample.passed()).isFalse();
    }

    @Test
    void relevanceRejectionDoesNotSkipFactChecking() {
        when(OrderAnswerEvaluationIT.writer.answer(anyString(), anyString(), anyString()))
            .thenReturn("An answer");
        when(OrderAnswerEvaluationIT.relevance.evaluate(any()))
            .thenReturn(new EvaluationResponse(false, "", Map.of()));
        when(OrderAnswerEvaluationIT.facts.evaluate(any()))
            .thenReturn(new EvaluationResponse(true, "", Map.of()));
        var sample = OrderAnswerEvaluationIT.evaluate("baseline", "prompt",
            OrderAnswerEvaluationIT.CASES.getFirst(), 1);
        verify(OrderAnswerEvaluationIT.facts).evaluate(any());
        assertThat(sample.relevant()).isFalse();
        assertThat(sample.supported()).isTrue();
        assertThat(sample.error()).isNull();
        assertThat(sample.passed()).isFalse();
    }

    @Test
    void blankAnswerIsRejectedWithoutCallingJudges() {
        when(OrderAnswerEvaluationIT.writer.answer(anyString(), anyString(), anyString()))
            .thenReturn(" ");
        var sample = OrderAnswerEvaluationIT.evaluate("baseline", "prompt",
            OrderAnswerEvaluationIT.CASES.getFirst(), 1);
        verifyNoInteractions(OrderAnswerEvaluationIT.relevance, OrderAnswerEvaluationIT.facts);
        assertThat(sample.error()).isNull();
        assertThat(sample.passed()).isFalse();
    }
}
