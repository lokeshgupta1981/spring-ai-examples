package com.howtodoinjava.ai.demo;

import java.util.List;
import javax.swing.Spring;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaApi.ChatRequest;
import org.springframework.ai.ollama.api.OllamaApi.Message;
import org.springframework.ai.ollama.api.OllamaApi.Message.Role;
import org.springframework.ai.ollama.api.OllamaModel;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class App implements CommandLineRunner {

  public static void main(String[] args) {
    SpringApplication.run(App.class);
  }

  @Autowired
  OllamaChatModel chatModel;

  @Override
  public void run(String... args) throws Exception {

    /*ChatResponse response = chatModel.call(
      new Prompt(
        "Generate the names of 5 famous pirates.",
        OllamaOptions.create()
          .withModel("gemma2")
          .withTemperature(0.4F)
      ));*/

    chatModel.stream(new Prompt(
      "Generate the names of 5 famous pirates.",
      OllamaOptions.create()
        .withModel("gemma2")
        .withTemperature(0.4F)
    )).subscribe(chatResponse -> {
      System.out.print(chatResponse.getResult().getOutput().getContent());
    });

    /*response.getResults()
      .stream()
      .map(generation -> generation.getOutput().getContent())
      .forEach(System.out::println);*/
  }

  /*@Bean
  OllamaChatModel ollamaChatModel(@Value("spring.ai.ollama.base-url") String baseUrl) {
    return new OllamaChatModel(new OllamaApi(baseUrl),
      OllamaOptions.create()
        .withModel("gemma")
        .withTemperature(0.4f));
  }*/
}

