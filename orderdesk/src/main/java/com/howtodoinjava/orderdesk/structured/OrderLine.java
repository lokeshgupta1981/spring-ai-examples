package com.howtodoinjava.orderdesk.structured;

// One product line that the customer mentions
public record OrderLine(String product, int quantity) {
}
