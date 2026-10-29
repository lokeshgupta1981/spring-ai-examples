package com.howtodoinjava.orderdesk.integration;

import java.net.http.HttpClient;
import java.security.Principal;
import java.time.Duration;
import java.util.Map;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

public class OrderApiTools {
    public record OrderStatus(String orderId, String status) {}
    public record Lookup(String outcome, OrderStatus order) {}
    private final RestClient api;

    public OrderApiTools(RestClient api) { this.api = api; }

    public static RestClient orderApi(String baseUrl) {
        var http = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(1)).build();
        var factory = new JdkClientHttpRequestFactory(http);
        factory.setReadTimeout(Duration.ofSeconds(2));
        return RestClient.builder().baseUrl(baseUrl).requestFactory(factory).build();
    }

    @Tool(description = "Read the current status of one order owned by the signed-in customer")
    public Lookup lookupOrder(
            @ToolParam(description = "Order ID such as ORD-1042") String orderId,
            ToolContext context) {
        Object identity = context.getContext().get("customerId");
        if (!(identity instanceof String customerId) || customerId.isBlank()) {
            throw new IllegalStateException("Authenticated customer context is required");
        }
        if (orderId == null || !orderId.matches("ORD-[0-9]{4,10}")) {
            return new Lookup("INVALID_ORDER_ID", null);
        }
        try {
            OrderStatus order = api.get()
                .uri("/customers/{customerId}/orders/{orderId}", customerId, orderId)
                .retrieve().body(OrderStatus.class);
            if (order == null) return new Lookup("UNAVAILABLE", null);
            return new Lookup("FOUND", order);
        } catch (HttpClientErrorException.NotFound failure) {
            return new Lookup("NOT_FOUND", null);
        } catch (ResourceAccessException failure) {
            return new Lookup("UNAVAILABLE", null);
        } catch (RestClientResponseException failure) {
            return new Lookup("UNAVAILABLE", null);
        }
    }

    public String ask(ChatClient client, String question, Principal principal) {
        if (principal == null || principal.getName() == null || principal.getName().isBlank()) {
            throw new IllegalArgumentException("An authenticated principal is required");
        }
        return client.prompt()
            .system("""
                Use lookupOrder for order status questions.
                Explain only the returned order data.
                NOT_FOUND means no accessible order was found.
                UNAVAILABLE means the lookup failed; do not guess its status.
                INVALID_ORDER_ID means ask for an order ID such as ORD-1042.
                """)
            .user(question).tools(this)
            .toolContext(Map.of("customerId", principal.getName()))
            .call().content();
    }
}
