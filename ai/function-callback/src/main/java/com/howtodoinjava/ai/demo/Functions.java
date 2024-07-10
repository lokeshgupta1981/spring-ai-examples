package com.howtodoinjava.ai.demo;

import com.howtodoinjava.ai.demo.StockQuoteService.Stock;
import java.util.function.Function;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;

@Configuration(proxyBeanMethods = false)
public class Functions {

  @Bean
  @Description("Get quote by stock name")
  public Function<Stock, Double> quoteByStockNameFunction(StockQuoteService stockQuoteService) {
    return stockQuoteService::getEquityPrice;
  }
}
