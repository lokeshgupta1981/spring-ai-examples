package com.howtodoinjava.supportdesk;

import org.springframework.ai.tool.annotation.Tool;

public class SupportTools {

    @Tool(description = "Get the status of a support ticket")
    public String getTicketStatus(String ticketId) {
        return "T-1001".equals(ticketId) ? "Waiting for the courier" : "Unknown ticket";
    }
}
