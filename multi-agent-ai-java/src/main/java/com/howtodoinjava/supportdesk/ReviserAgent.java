package com.howtodoinjava.supportdesk;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface ReviserAgent {

    @UserMessage("""
            Rewrite this customer support reply so it no longer makes the promise named in the note.
            Keep the ticket id at the start and the line "OrderDesk Support" at the end.
            The customer must not see the note, so never mention it, a reviewer or any rules.
            Return only the corrected reply.
            Note: {{review}}
            Reply: {{draft}}
            """)
    @Agent(description = "Rewrites a draft reply to remove an exact promise", outputKey = "draft")
    String revise(@V("review") String review, @V("draft") String draft);
}
