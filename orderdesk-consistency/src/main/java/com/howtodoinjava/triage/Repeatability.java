package com.howtodoinjava.triage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.howtodoinjava.triage.TicketClassifier.TicketTriage;

public class Repeatability {
    public record Decision(TicketClassifier.Category category, TicketClassifier.Priority priority) {}
    public record Report(int attempts, int failures, int distinctDecisions,
                         double agreement, double accuracy) {}

    public static Report measure(List<TicketTriage> results, Decision expected) {
        if (results.isEmpty()) throw new IllegalArgumentException("At least one attempt is required");
        Map<Decision, Integer> counts = new HashMap<>();
        int failures = 0;
        for (TicketTriage result : results) {
            if (result == null) { failures++; continue; }
            Decision decision = new Decision(result.category(), result.priority());
            counts.merge(decision, 1, Integer::sum);
        }
        int mostFrequent = counts.values().stream().mapToInt(Integer::intValue).max().orElse(0);
        return new Report(results.size(), failures, counts.size(),
            mostFrequent / (double) results.size(),
            counts.getOrDefault(expected, 0) / (double) results.size());
    }
}
