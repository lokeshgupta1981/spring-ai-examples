package com.howtodoinjava.orderdesk.vectorstore;

import java.util.List;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PolicySearchController {

    private final VectorStore vectorStore;

    PolicySearchController(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    @GetMapping("/search")
    List<SearchHit> search(@RequestParam String query,
                           @RequestParam(defaultValue = "3") int topK,
                           @RequestParam(defaultValue = "0.0") double threshold,
                           @RequestParam(required = false) String topic) {

        SearchRequest.Builder request = SearchRequest.builder()
            .query(query)
            .topK(topK)
            .similarityThreshold(threshold);
        if (topic != null) {
            // the builder treats topic as a value, never as part of the expression
            request.filterExpression(new FilterExpressionBuilder().eq("topic", topic).build());
        }

        List<Document> documents = vectorStore.similaritySearch(request.build());
        return documents.stream()
            .map(d -> new SearchHit(
                Math.round(d.getScore() * 1000) / 1000.0,
                String.valueOf(d.getMetadata().get("topic")),
                d.getText()))
            .toList();
    }

    record SearchHit(double score, String topic, String text) {
    }
}
