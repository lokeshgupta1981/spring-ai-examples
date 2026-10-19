package com.howtodoinjava.orderdesk.structured;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

// The support ticket that the model fills in from a customer message
public record TicketTriage(
    @JsonPropertyDescription("Order id such as ORD-1042, or NONE if the message has none")
    String orderId,
    Category category,
    Priority priority,
    @JsonPropertyDescription("One sentence of at most 15 words")
    String summary) {

    public enum Category { RETURN, DELIVERY, PAYMENT, WARRANTY, OTHER }

    public enum Priority { LOW, MEDIUM, HIGH }
}
