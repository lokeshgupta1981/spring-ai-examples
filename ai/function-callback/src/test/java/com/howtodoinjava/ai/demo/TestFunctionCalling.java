package com.howtodoinjava.ai.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.TestConfiguration;

@TestConfiguration(proxyBeanMethods = false)
public class TestFunctionCalling {
  public static void main(String[] args) {
    SpringApplication.from(SpringAiFunctionCallbackApplication::main).with(SpringAiFunctionCallbackApplicationTest.class).run(args);
  }
}
