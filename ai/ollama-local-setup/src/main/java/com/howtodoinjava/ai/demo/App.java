package com.howtodoinjava.ai.demo;

import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaApi;
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

    OllamaOptions ollamaOptions = OllamaOptions.builder().model("gemma").temperature(0.4d).build();

    ChatResponse response = chatModel.call(new Prompt("Generate the names of 5 famous pirates.", ollamaOptions));

    OllamaOptions ollamaOptions2 = OllamaOptions.builder().model("gemma").temperature(0.4d).build();

    chatModel.stream(new Prompt("Generate the names of 5 famous pirates.", ollamaOptions2)).subscribe(chatResponse -> {
      System.out.print(chatResponse.getResult().getOutput().getText());
    });

    /*response.getResults()
      .stream()
      .map(generation -> generation.getOutput().getContent())
      .forEach(System.out::println);*/
  }

  @Bean
  OllamaChatModel ollamaChatModel(@Value("spring.ai.ollama.base-url") String baseUrl) {

    OllamaApi ollamaApi = OllamaApi.builder().baseUrl(baseUrl).build();

    OllamaOptions ollamaOptions = OllamaOptions.builder().model("gemma").temperature(0.4d).build();

    return OllamaChatModel.builder().ollamaApi(ollamaApi).defaultOptions(ollamaOptions).build();
  }
}

