package com.howtodoinjava.ai.demo;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CustomExceptionHandler {

  @ExceptionHandler(AiException.class)
  public ProblemDetail handle(AiException ex) {
    return ProblemDetail.forStatusAndDetail(HttpStatus.EXPECTATION_FAILED, ex.getMessage());
  }

}
