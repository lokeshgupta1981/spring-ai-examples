package com.howtodoinjava.orderdesk.chat;

public record ShippingQuote(String city, boolean delivered, int days, int feeInRupees) {
}
