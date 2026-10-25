package com.howtodoinjava.supportdesk;

import com.embabel.agent.api.annotation.LlmTool;

public class SupportTools {

    @LlmTool(description = "Get the status of a support ticket")
    public String getTicketStatus(String ticketId) {
        return "T-1001".equals(ticketId) ? "Waiting for the courier" : "Unknown ticket";
    }
}
