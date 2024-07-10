package com.howtodoinjava.ai.demo;

import org.springframework.ai.image.ImageModel;
import org.springframework.ai.openai.OpenAiImageModel;
import org.springframework.ai.openai.api.OpenAiImageApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfiguration {

  @Value("${spring.ai.openai.api-key}")
  String apiKey;

  @Bean
  ImageModel imageModel() {
    return new OpenAiImageModel(new OpenAiImageApi(apiKey));
  }
}
