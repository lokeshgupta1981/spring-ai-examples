package com.howtodoinjava.ai.demo;

import java.util.List;

public record HealthRecord(String fullName, List<Observation> observations, Diagnosis diagnosis) {
  public record Observation(String type, String content) {}
  public record Diagnosis(String content) {}
}
