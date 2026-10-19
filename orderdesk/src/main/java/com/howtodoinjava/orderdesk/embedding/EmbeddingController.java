package com.howtodoinjava.orderdesk.embedding;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EmbeddingController {

    static final List<String> POLICY_SENTENCES = List.of(
        "You can return most items within 10 days of delivery.",
        "Delivery is free for orders above INR 499.",
        "Electronics come with a 1-year manufacturer warranty.",
        "Cash on delivery is available for orders up to INR 5,000.");

    private final EmbeddingModel embeddingModel;

    EmbeddingController(EmbeddingModel embeddingModel) {
        this.embeddingModel = embeddingModel;
    }

    @GetMapping("/embed")
    Map<String, Object> embed(@RequestParam String text) {
        float[] vector = embeddingModel.embed(text);
        return Map.of(
            "dimensions", vector.length,
            "firstFive", Arrays.copyOf(vector, 5));
    }

    @GetMapping("/similar")
    List<SentenceScore> similar(@RequestParam String question) {
        float[] questionVector = embeddingModel.embed(question);
        List<float[]> sentenceVectors = embeddingModel.embed(POLICY_SENTENCES);

        return POLICY_SENTENCES.stream()
            .map(s -> new SentenceScore(s,
                cosineSimilarity(questionVector, sentenceVectors.get(POLICY_SENTENCES.indexOf(s)))))
            .sorted(Comparator.comparingDouble(SentenceScore::score).reversed())
            .toList();
    }

    static double cosineSimilarity(float[] a, float[] b) {
        double dot = 0, normA = 0, normB = 0;
        for (int i = 0; i < a.length; i++) {
            dot += a[i] * b[i];
            normA += a[i] * a[i];
            normB += b[i] * b[i];
        }
        return Math.round(dot / (Math.sqrt(normA) * Math.sqrt(normB)) * 1000) / 1000.0;
    }

    record SentenceScore(String sentence, double score) {
    }
}
