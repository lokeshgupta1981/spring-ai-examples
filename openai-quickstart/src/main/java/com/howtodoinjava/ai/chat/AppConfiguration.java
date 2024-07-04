package com.howtodoinjava.ai.chat;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class AppConfiguration {

  @Bean
  @Primary
  ChatModel chatModel(@Value("${spring.ai.openai.api-key}") String apiKey) {
    return new OpenAiChatModel(new OpenAiApi(apiKey));
  }
}
