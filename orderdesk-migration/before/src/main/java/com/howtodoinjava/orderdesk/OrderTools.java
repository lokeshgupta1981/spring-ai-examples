package com.howtodoinjava.orderdesk;

import java.util.Map;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

@Component
public class OrderTools {

    private static final Map<String, String> STATUS = Map.of(
        "ORD-1042", "shipped on 14 October, arriving 18 October",
        "ORD-1043", "packed, ships tomorrow");

    @Tool(description = "Find the delivery status of an order")
    String orderStatus(@ToolParam(description = "Order id such as ORD-1042") String orderId) {
        if (orderId == null || orderId.isBlank()) {
            return "no order id given; ask the customer for it";
        }
        return STATUS.getOrDefault(orderId, "no order with this id");
    }
}
