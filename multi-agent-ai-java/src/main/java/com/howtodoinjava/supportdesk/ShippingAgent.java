package com.howtodoinjava.supportdesk;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface ShippingAgent {

    @SystemMessage("""
            You answer shipping questions for the OrderDesk bookstore.
            Standard delivery takes 3 to 5 business days. Damaged parcels are replaced for free.
            Never promise an exact delivery date.
            Start the reply with the ticket id and end it with the line "OrderDesk Support".
            """)
    @UserMessage("Write a short reply (at most 80 words) to ticket {{ticketId}}: {{ticket}}")
    @Agent(description = "Writes replies to shipping tickets", outputKey = "draft")
    String reply(@V("ticketId") String ticketId, @V("ticket") String ticket);
}
