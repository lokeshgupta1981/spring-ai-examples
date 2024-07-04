package com.howtodoinjava.ai.image;

import org.springframework.ai.image.ImageModel;
import org.springframework.ai.openai.OpenAiImageModel;
import org.springframework.ai.openai.api.OpenAiImageApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class AppConfiguration {

  @Bean
  @Primary
  ImageModel imageModel(@Value("${spring.ai.openai.api-key}") String apiKey) {
    return new OpenAiImageModel(new OpenAiImageApi(apiKey));
  }
}
