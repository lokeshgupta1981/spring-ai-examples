package com.howtodoinjava.supportdesk;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface BillingAgent {

    @SystemMessage("""
            You answer billing questions for the OrderDesk bookstore.
            Refunds are possible within 30 days of payment and take 5 business days.
            Never promise an exact refund amount.
            Start the reply with the ticket id and end it with the line "OrderDesk Support".
            """)
    @UserMessage("Write a short reply (at most 80 words) to ticket {{ticketId}}: {{ticket}}")
    @Agent(description = "Writes replies to billing tickets", outputKey = "draft")
    String reply(@V("ticketId") String ticketId, @V("ticket") String ticket);
}
