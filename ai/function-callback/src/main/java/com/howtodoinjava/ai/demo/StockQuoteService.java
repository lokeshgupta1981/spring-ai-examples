package com.howtodoinjava.ai.demo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service
public class StockQuoteService {

  private static final Map<Stock, Double> data = new ConcurrentHashMap<>();

  static {
    data.put(new Stock("Google"), 101.00);
    data.put(new Stock("Microsoft"), 102.00);
    data.put(new Stock("Tesla"), 103.00);
    data.put(new Stock("OpenAI"), 104.00);
    data.put(new Stock("HDFC"), 105.00);
  }

  Double getEquityPrice(Stock stock) {
    return data.keySet().stream()
      .filter(s -> s.name().equalsIgnoreCase(stock.name()))
      .map(s -> data.get(s))
      .findFirst()
      .orElse(-1.0);
  }

  public record Stock(String name) {

  }
}
