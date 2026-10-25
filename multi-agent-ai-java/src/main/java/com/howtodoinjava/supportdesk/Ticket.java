package com.howtodoinjava.supportdesk;

import java.util.List;

public record Ticket(String id, String text, Department expected) {

    static final List<Ticket> TEST_SET = List.of(
            new Ticket("T-1001", "I was charged twice for order 5521. Please fix it.", Department.BILLING),
            new Ticket("T-1002", "My parcel was supposed to arrive on Monday and it still isn't here.", Department.SHIPPING),
            new Ticket("T-1003", "Can I get a refund for a book I returned last week?", Department.BILLING),
            new Ticket("T-1004", "The courier left the box in the rain and two books are damaged.", Department.SHIPPING),
            new Ticket("T-1005", "Where can I download the invoice for my company order?", Department.BILLING),
            new Ticket("T-1006", "The tracking number you sent me doesn't work.", Department.SHIPPING),
            new Ticket("T-1007", "My card payment failed but the money left my account.", Department.BILLING),
            new Ticket("T-1008", "Can you deliver to a different address than the one on my order?", Department.SHIPPING),
            new Ticket("T-1009", "I used a coupon but I was charged the full price.", Department.BILLING),
            new Ticket("T-1010", "The parcel shows as delivered but I never received it.", Department.SHIPPING));
}
