package com.howtodoinjava.supportdesk;

import io.agentscope.core.tool.Tool;
import io.agentscope.core.tool.ToolParam;

public class SupportTools {

    @Tool(name = "get_ticket_status", description = "Get the status of a support ticket")
    public String getTicketStatus(
            @ToolParam(name = "ticketId", description = "The ticket id, such as T-1001") String ticketId) {
        return "T-1001".equals(ticketId) ? "Waiting for the courier" : "Unknown ticket";
    }
}
