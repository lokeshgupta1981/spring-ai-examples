package com.howtodoinjava.orderdesk.evaluation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.evaluation.FactCheckingEvaluator;
import org.springframework.ai.chat.evaluation.RelevancyEvaluator;
import org.springframework.ai.document.Document;
import org.springframework.ai.evaluation.EvaluationRequest;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaChatOptions;
import static org.assertj.core.api.Assertions.assertThat;

@EnabledIfEnvironmentVariable(named = "RUN_LLM_EVAL", matches = "true")
class OrderAnswerEvaluationIT {
    static final String BASELINE = "Answer the customer's question about orders.";
    static final String REVISED = """
        Answer the customer's question using only the supplied order data.
        Preserve counts and city names. Do not infer delivery status or dates.
        If the data does not answer the question, say that it is not provided.
        Keep the answer to one or two sentences.
        """;
    static final String QUESTION = "How many orders went to Pune?";
    static final String CONTEXT = "Exactly three orders went to Pune. Delivery dates are not provided.";
    static OrderAnswerWriter writer;
    static RelevancyEvaluator relevance;
    static FactCheckingEvaluator facts;

    @BeforeAll
    static void setUp() {
        writer = new OrderAnswerWriter(client("qwen2.5:3b", 0.7).build());
        relevance = new RelevancyEvaluator(client("qwen2.5:7b", 0.0));
        facts = FactCheckingEvaluator.builder(client("qwen2.5:7b", 0.0)).build();
    }

    static ChatClient.Builder client(String model, double temperature) {
        OllamaApi api = OllamaApi.builder()
            .baseUrl(System.getenv().getOrDefault("OLLAMA_BASE_URL", "http://localhost:11434"))
            .build();
        OllamaChatModel chatModel = OllamaChatModel.builder()
            .ollamaApi(api)
            .options(OllamaChatOptions.builder()
                .model(model).temperature(temperature).build())
            .build();
        return ChatClient.builder(chatModel);
    }

    static EvaluationRequest request(String question, String context, String answer) {
        return new EvaluationRequest(question, List.of(new Document(context)), answer);
    }

    @Test
    void generatesAnAnswer() {
        String answer = writer.answer(BASELINE, QUESTION, CONTEXT);
        assertThat(answer).isNotBlank(); // Checks for text, not a correct answer.
    }

    @Test
    void generatedAnswerIsRelevant() {
        String answer = writer.answer(BASELINE, QUESTION, CONTEXT);
        assertThat(answer).isNotBlank(); // Fails before evaluation if no text is returned.
        var result = relevance.evaluate(request(QUESTION, CONTEXT, answer));
        // Passes only if the judge considers the answer relevant.
        assertThat(result.isPass()).as("Answer was %s", answer).isTrue();
    }

    @Test
    void unrelatedAnswerIsRejected() {
        String answer = "Java supports interfaces and classes.";
        var result = relevance.evaluate(request(QUESTION, CONTEXT, answer));
        // Passes when the unrelated Java answer is rejected.
        assertThat(result.isPass()).as("Unrelated answer was accepted").isFalse();
    }

    @Test
    void generatedAnswerIsSupported() {
        String answer = writer.answer(BASELINE, QUESTION, CONTEXT);
        assertThat(answer).isNotBlank(); // Fails before evaluation if no text is returned.
        var result = facts.evaluate(request(QUESTION, CONTEXT, answer));
        // Passes only if the judge considers the claims supported by the context.
        assertThat(result.isPass()).as("Answer was %s", answer).isTrue();
    }

    record LabeledAnswer(String answer, boolean expected) { }

    static Stream<LabeledAnswer> labeledAnswers() {
        return Stream.of(
            new LabeledAnswer("Three orders went to Pune.", true),
            new LabeledAnswer("Pune received 3 orders.", true),
            new LabeledAnswer("Two orders went to Pune.", false),
            new LabeledAnswer("No orders went to Pune.", false),
            new LabeledAnswer("Three orders went to Pune and arrived yesterday.", false));
    }

    @ParameterizedTest
    @MethodSource("labeledAnswers")
    void factJudgeMatchesLabels(LabeledAnswer example) {
        boolean observed = facts.evaluate(request(QUESTION, CONTEXT, example.answer())).isPass();
        System.out.printf("expected=%s observed=%s answer=%s%n",
            example.expected(), observed, example.answer());
        // Passes when the judge agrees with our label; either disagreement fails.
        assertThat(observed).as("Answer was %s", example.answer()).isEqualTo(example.expected());
    }

    @Test
    void calibrateRelevance() {
        boolean relevant = relevance.evaluate(request(QUESTION, CONTEXT,
            "Three orders went to Pune.")).isPass();
        boolean unrelated = relevance.evaluate(request(QUESTION, CONTEXT,
            "Java supports interfaces and classes.")).isPass();
        assertThat(relevant).as("Known relevant answer").isTrue(); // Must accept the Pune answer.
        assertThat(unrelated).as("Known unrelated answer").isFalse(); // Must reject the Java answer.
    }

    record Case(String id, String question, String context) { }

    static final List<Case> CASES = List.of(
        new Case("pune-count", QUESTION, CONTEXT),
        new Case("mumbai-count", "How many orders went to Mumbai?",
            "Exactly two orders went to Mumbai. Delivery dates are not provided."),
        new Case("pune-delivery", "When did the Pune orders arrive?", CONTEXT));

    record Sample(String variant, String caseId, int attempt, String question,
                  String context, String answer, Boolean relevant,
                  Boolean supported, String error) {
        boolean passed() {
            return error == null && Boolean.TRUE.equals(relevant)
                && Boolean.TRUE.equals(supported);
        }
    }

    static Sample evaluate(String variant, String prompt, Case example, int attempt) {
        String answer = null;
        Boolean relevant = null;
        String stage = "generation";
        try {
            answer = writer.answer(prompt, example.question(), example.context());
            if (answer == null || answer.isBlank()) {
                return new Sample(variant, example.id(), attempt, example.question(),
                    example.context(), answer, false, false, null);
            }
            var input = request(example.question(), example.context(), answer);
            stage = "relevance evaluation";
            relevant = relevance.evaluate(input).isPass();
            stage = "factual evaluation";
            boolean supported = facts.evaluate(input).isPass();
            return new Sample(variant, example.id(), attempt, example.question(),
                example.context(), answer, relevant, supported, null);
        } catch (RuntimeException failure) {
            return new Sample(variant, example.id(), attempt, example.question(),
                example.context(), answer, relevant, null,
                stage + " / " + failure.getClass().getSimpleName() + " / " + failure.getMessage());
        }
    }

    @Test
    void repeatedAnswers() throws IOException {
        List<Sample> samples = new ArrayList<>();
        for (Case example : CASES) {
            for (int attempt = 1; attempt <= 3; attempt++) {
                samples.add(evaluate("baseline", BASELINE, example, attempt));
            }
        }
        writeReport("repeated-answers", samples);
        summarize("baseline", samples);
        // Fails on execution errors. Rejected answers are recorded, not asserted here.
        assertThat(samples).allMatch(s -> s.error() == null);
    }

    static void writeReport(String name, List<Sample> samples) throws IOException {
        Path directory = Path.of("target", "evaluations");
        Files.createDirectories(directory);
        StringBuilder report = new StringBuilder();
        for (Sample s : samples) {
            report.append("Variant ").append(s.variant()).append(" / case ").append(s.caseId())
                .append(" / attempt ").append(s.attempt()).append('\n')
                .append("Question\n").append(s.question()).append('\n')
                .append("Context\n").append(s.context()).append('\n')
                .append("Answer\n").append(s.answer()).append('\n')
                .append("Relevant ").append(s.relevant())
                .append(" / supported ").append(s.supported())
                .append(" / error ").append(s.error()).append("\n\n");
        }
        Files.writeString(directory.resolve(name + ".txt"), report);
    }

    static void summarize(String variant, List<Sample> samples) {
        var selected = samples.stream().filter(s -> s.variant().equals(variant)).toList();
        long passed = selected.stream().filter(Sample::passed).count();
        long errors = selected.stream().filter(s -> s.error() != null).count();
        long rejected = selected.size() - passed - errors;
        System.out.printf("%s accepted=%d rejected=%d errors=%d total=%d passRate=%.1f%%%n",
            variant, passed, rejected, errors, selected.size(),
            100.0 * passed / selected.size());
    }

    @Test
    void comparePrompts() throws IOException {
        List<Sample> samples = new ArrayList<>();
        for (Case example : CASES) {
            for (int attempt = 1; attempt <= 3; attempt++) {
                if (attempt % 2 == 1) {
                    samples.add(evaluate("baseline", BASELINE, example, attempt));
                    samples.add(evaluate("revised", REVISED, example, attempt));
                } else {
                    samples.add(evaluate("revised", REVISED, example, attempt));
                    samples.add(evaluate("baseline", BASELINE, example, attempt));
                }
            }
        }
        writeReport("prompt-comparison", samples);
        summarize("baseline", samples);
        summarize("revised", samples);
        // Checks execution only; a pass does not mean the revised prompt is better.
        assertThat(samples).allMatch(s -> s.error() == null);
    }
}
