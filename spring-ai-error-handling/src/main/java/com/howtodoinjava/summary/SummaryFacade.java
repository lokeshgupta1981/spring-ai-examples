package com.howtodoinjava.summary;

import org.springframework.ai.retry.TransientAiException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;

@Service
public class SummaryFacade {
    public record SummaryResult(String text, boolean degraded) {}
    private final SummaryService summaries;

    public SummaryFacade(SummaryService summaries) {
        this.summaries = summaries;
    }

    public SummaryResult summarize(String facts) {
        try {
            return new SummaryResult(summaries.summarize(facts), false);
        } catch (TransientAiException | ResourceAccessException failure) {
            return new SummaryResult(
                "The summary is temporarily unavailable. Please read the order details.", true);
        }
    }
}
