package com.howtodoinjava.supportdesk;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface TriageAgent {

    @UserMessage("""
            Decide which department handles this support ticket.
            BILLING handles payments, invoices, refunds and charges.
            SHIPPING handles deliveries, couriers, tracking and damaged parcels.
            Ticket: {{ticket}}
            """)
    @Agent(description = "Labels a ticket as BILLING or SHIPPING", outputKey = "department")
    Department classify(@V("ticket") String ticket);
}
