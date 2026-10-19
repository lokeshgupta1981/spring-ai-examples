package com.howtodoinjava.orderdesk.tools;

import java.time.LocalDate;
import java.util.List;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

@Component
public class OrderTools {

    private final JdbcClient jdbcClient;

    OrderTools(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Tool(description = "Get the status, city, total and dates of one order by its id")
    public OrderInfo getOrderStatus(
            @ToolParam(description = "Order id, for example ORD-1042") String orderId) {
        return jdbcClient.sql("""
                SELECT order_id, customer_name, city, status, total_inr, placed_on, delivered_on
                FROM orders WHERE order_id = ?""")
            .param(orderId.trim().toUpperCase())
            .query(OrderInfo.class)
            .optional()
            .orElseThrow(() -> new IllegalArgumentException("No order with id " + orderId));
    }

    @Tool(description = "List all orders of one customer by the customer's full name")
    public List<OrderInfo> listOrders(
            @ToolParam(description = "Customer's full name, for example Priya Sharma")
            String customerName) {
        return jdbcClient.sql("""
                SELECT order_id, customer_name, city, status, total_inr, placed_on, delivered_on
                FROM orders WHERE LOWER(customer_name) = LOWER(?) ORDER BY placed_on""")
            .param(customerName.trim())
            .query(OrderInfo.class)
            .list();
    }

    @Tool(description = """
        Start the cancellation of an order. It does not cancel the order:
        it returns the text the customer must confirm on the order page.""")
    public String requestCancellation(
            @ToolParam(description = "Order id, for example ORD-1042") String orderId) {
        OrderInfo order = getOrderStatus(orderId);
        if (!order.status().equals("PLACED")) {
            return "Order " + order.orderId() + " is " + order.status()
                + " and can't be cancelled.";
        }
        return "To cancel " + order.orderId() + " (INR " + order.totalInr()
            + "), the customer must click Confirm cancellation on the order page.";
    }

    public record OrderInfo(String orderId, String customerName, String city, String status,
                            double totalInr, LocalDate placedOn, LocalDate deliveredOn) {
    }
}
