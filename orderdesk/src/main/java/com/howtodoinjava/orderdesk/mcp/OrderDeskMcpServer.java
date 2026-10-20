package com.howtodoinjava.orderdesk.mcp;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Pattern;

import org.springframework.ai.mcp.annotation.McpArg;
import org.springframework.ai.mcp.annotation.McpPrompt;
import org.springframework.ai.mcp.annotation.McpResource;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import io.modelcontextprotocol.spec.McpSchema.GetPromptResult;
import io.modelcontextprotocol.spec.McpSchema.PromptMessage;
import io.modelcontextprotocol.spec.McpSchema.Role;
import io.modelcontextprotocol.spec.McpSchema.TextContent;

import com.howtodoinjava.orderdesk.tools.OrderTools;
import com.howtodoinjava.orderdesk.tools.OrderTools.OrderInfo;

// Exposes OrderDesk's order lookups, policies and a reply prompt over MCP
@Component
public class OrderDeskMcpServer {

    private static final Pattern TOPIC = Pattern.compile("returns|shipping|warranty|payments");

    private final OrderTools orderTools;

    OrderDeskMcpServer(OrderTools orderTools) {
        this.orderTools = orderTools;
    }

    @McpTool(name = "get_order_status",
        description = "Get the status, city, total and dates of one order by its id",
        annotations = @McpTool.McpAnnotations(readOnlyHint = true, destructiveHint = false,
            idempotentHint = true, openWorldHint = false))
    public OrderInfo getOrderStatus(
            @McpToolParam(description = "Order id, for example ORD-1042") String orderId) {
        return orderTools.getOrderStatus(orderId);
    }

    @McpTool(name = "list_orders",
        description = "List all orders of one customer by the customer's full name",
        annotations = @McpTool.McpAnnotations(readOnlyHint = true, destructiveHint = false,
            idempotentHint = true, openWorldHint = false))
    public List<OrderInfo> listOrders(
            @McpToolParam(description = "Customer's full name, for example Priya Sharma")
            String customerName) {
        return orderTools.listOrders(customerName);
    }

    @McpResource(uri = "policy://{topic}", name = "OrderDesk policy",
        description = "One OrderDesk policy as Markdown: returns, shipping, warranty or payments",
        mimeType = "text/markdown")
    public String policy(String topic) throws IOException {
        if (!TOPIC.matcher(topic).matches()) {
            throw new IllegalArgumentException("Unknown policy: " + topic);
        }
        return new ClassPathResource("policies/" + topic + ".md")
            .getContentAsString(StandardCharsets.UTF_8);
    }

    @McpPrompt(name = "support_reply",
        description = "Draft a short, polite reply to a customer about one order")
    public GetPromptResult supportReply(
            @McpArg(name = "orderId", description = "Order id", required = true) String orderId,
            @McpArg(name = "complaint", description = "What the customer wrote", required = true)
            String complaint) {
        String text = """
            You are an OrderDesk support agent. A customer wrote about order %s:
            "%s"
            First call get_order_status for this order. Then write a reply of at most
            four sentences that states the order status and the next step.
            """.formatted(orderId, complaint);
        // a String return becomes an assistant message; a prompt should be a user message
        return GetPromptResult.builder(
                List.of(new PromptMessage(Role.USER, TextContent.builder(text).build())))
            .description("Reply to a customer about one order")
            .build();
    }
}
