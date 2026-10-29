package com.howtodoinjava.summary;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@RestController
public class SummaryController {
    private final SummaryService summaries;
    private final SummaryFacade fallback;
    private final StreamingSummaryService streaming;

    public SummaryController(SummaryService summaries, SummaryFacade fallback,
            StreamingSummaryService streaming) {
        this.summaries = summaries;
        this.fallback = fallback;
        this.streaming = streaming;
    }

    @GetMapping("/summary")
    public Mono<String> summarize(@RequestParam String facts) {
        return Mono.fromCallable(() -> summaries.summarize(facts))
            .subscribeOn(Schedulers.boundedElastic());
    }

    @GetMapping("/summary/fallback")
    public Mono<SummaryFacade.SummaryResult> fallback(@RequestParam String facts) {
        return Mono.fromCallable(() -> fallback.summarize(facts))
            .subscribeOn(Schedulers.boundedElastic());
    }

    @GetMapping(value = "/summary/stream", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<StreamingSummaryService.SummaryEvent> stream(@RequestParam String facts) {
        return streaming.summarize(facts);
    }
}
