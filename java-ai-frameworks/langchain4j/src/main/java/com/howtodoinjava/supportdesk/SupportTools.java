package com.howtodoinjava.supportdesk;

import dev.langchain4j.agent.tool.Tool;

public class SupportTools {

    @Tool("Get the status of a support ticket")
    public String getTicketStatus(String ticketId) {
        return "T-1001".equals(ticketId) ? "Waiting for the courier" : "Unknown ticket";
    }
}
