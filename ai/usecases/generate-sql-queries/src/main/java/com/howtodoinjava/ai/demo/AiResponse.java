package com.howtodoinjava.ai.demo;

import java.util.List;
import java.util.Map;

public record AiResponse(String sqlQuery, List<Map<String, Object>> results) { }
