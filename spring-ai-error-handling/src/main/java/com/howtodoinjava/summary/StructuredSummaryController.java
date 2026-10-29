package com.howtodoinjava.summary;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@RestController
public class StructuredSummaryController {
    private final StructuredSummaryService summaries;

    public StructuredSummaryController(StructuredSummaryService summaries) {
        this.summaries = summaries;
    }

    @GetMapping("/summary/structured")
    public Mono<StructuredSummaryService.OrderSummary> summarize(@RequestParam String facts) {
        return Mono.fromCallable(() -> summaries.summarize(facts))
            .subscribeOn(Schedulers.boundedElastic())
            .onErrorMap(RuntimeException.class, failure -> new ResponseStatusException(
                HttpStatus.BAD_GATEWAY, "Unable to generate a usable order summary.", failure));
    }
}
