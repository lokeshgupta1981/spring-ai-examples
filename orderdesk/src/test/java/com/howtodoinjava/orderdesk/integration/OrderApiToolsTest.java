package com.howtodoinjava.orderdesk.integration;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.support.ToolCallbacks;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

class OrderApiToolsTest {
    @Test
    void lookupUsesTrustedCustomerContext() {
        var builder = RestClient.builder().baseUrl("https://orders.example");
        var server = MockRestServiceServer.bindTo(builder).build();
        server.expect(requestTo("https://orders.example/customers/alice/orders/ORD-1042"))
            .andRespond(withSuccess("""
                {"orderId":"ORD-1042","status":"PACKED"}
                """, MediaType.APPLICATION_JSON));
        var tool = new OrderApiTools(builder.build());
        var result = tool.lookupOrder("ORD-1042", new ToolContext(Map.of("customerId", "alice")));
        assertThat(result.order().status()).isEqualTo("PACKED"); // Status comes from the API.
        server.verify(); // The request used Alice's trusted customer scope.
    }

    @Test
    void notFoundIsDifferentFromUnavailable() {
        var builder = RestClient.builder().baseUrl("https://orders.example");
        var server = MockRestServiceServer.bindTo(builder).build();
        server.expect(requestTo("https://orders.example/customers/alice/orders/ORD-1042"))
            .andRespond(withResourceNotFound());
        var tool = new OrderApiTools(builder.build());
        var result = tool.lookupOrder("ORD-1042", new ToolContext(Map.of("customerId", "alice")));
        assertThat(result.outcome()).isEqualTo("NOT_FOUND"); // No accessible order, not an outage.
        assertThat(result.order()).isNull(); // We never invent order details.
        server.verify();
    }

    @Test
    void customerIdentityIsNotAModelArgument() {
        var tool = new OrderApiTools(RestClient.create());
        var callback = ToolCallbacks.from(tool)[0];
        assertThat(callback.getToolDefinition().inputSchema())
            .contains("orderId").doesNotContain("customerId"); // The model cannot choose a customer.
        assertThatThrownBy(() -> tool.lookupOrder("ORD-1042", new ToolContext(Map.of())))
            .isInstanceOf(IllegalStateException.class); // Missing identity fails before an HTTP call.
    }
}
