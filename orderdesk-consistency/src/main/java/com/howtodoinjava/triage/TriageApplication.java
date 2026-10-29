package com.howtodoinjava.triage;

import java.util.ArrayList;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Value;

@SpringBootApplication
public class TriageApplication {
    public static void main(String[] args) { SpringApplication.run(TriageApplication.class, args); }

    @Bean
    @ConditionalOnProperty(name="app.measure", havingValue="true")
    CommandLineRunner measure(TicketClassifier classifier, @Value("${app.runs:10}") int runs) {
        return args -> {
            var results = new ArrayList<TicketClassifier.TicketTriage>();
            for (int i = 0; i < runs; i++) {
                try { results.add(classifier.classify("My parcel is three days late.")); }
                catch (RuntimeException failure) {
                    results.add(null);
                    System.err.printf("Attempt %d failed (%s)%n", i + 1,
                        failure.getClass().getSimpleName());
                }
            }
            System.out.println(Repeatability.measure(results, new Repeatability.Decision(
                TicketClassifier.Category.DELIVERY, TicketClassifier.Priority.NORMAL)));
        };
    }
}
