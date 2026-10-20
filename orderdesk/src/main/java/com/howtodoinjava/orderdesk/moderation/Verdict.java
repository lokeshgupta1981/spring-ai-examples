package com.howtodoinjava.orderdesk.moderation;

import java.util.List;

// The answer of the safety model: safe, or unsafe with category codes such as S1
public record Verdict(boolean safe, List<String> categories) {
}
