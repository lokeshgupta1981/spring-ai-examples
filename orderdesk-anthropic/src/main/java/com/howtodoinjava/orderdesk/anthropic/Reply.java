package com.howtodoinjava.orderdesk.anthropic;

// The answer plus the facts that differ between models
public record Reply(String model, String answer, Integer inputTokens, Integer outputTokens) {
}
