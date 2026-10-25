package com.howtodoinjava.supportdesk;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface ReviewerAgent {

    @UserMessage("""
            You review support replies before they are sent.
            Check one thing only: does the reply promise an exact money amount for a refund
            (for example "$25.99") or an exact calendar date for a delivery (for example "on 3 November")?
            Saying "within 5 business days" or "3 to 5 business days" is fine and is not an exact date.
            If the reply makes no exact promise, answer with only the word APPROVED.
            Otherwise, answer with one sentence that quotes the exact promise to remove.
            Reply: {{draft}}
            """)
    @Agent(description = "Checks that a draft reply makes no exact refund or delivery promise", outputKey = "review")
    String review(@V("draft") String draft);
}
