package com.howtodoinjava.orderdesk.integration;

import java.security.Principal;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class AuthorizedOrderLookup {
    public record StoredOrder(String orderId, String customerId, String status) {}
    public record OrderStatus(String orderId, String status) {}
    public interface OrderRepository {
        Optional<StoredOrder> findById(String orderId);
    }
    private final OrderRepository orders;

    public AuthorizedOrderLookup(OrderRepository orders) { this.orders = orders; }

    public OrderStatus lookup(String customerId, String orderId, Principal principal) {
        if (principal == null || principal.getName() == null || principal.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        if (!principal.getName().equals(customerId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        StoredOrder order = orders.findById(orderId)
            .filter(found -> found.customerId().equals(principal.getName()))
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return new OrderStatus(order.orderId(), order.status());
    }
}
