package com.howtodoinjava.ai.demo.config;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.image.ImageModel;
import org.springframework.ai.openai.OpenAiAudioSpeechModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiImageModel;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.openai.api.OpenAiAudioApi;
import org.springframework.ai.openai.api.OpenAiImageApi;
import org.springframework.ai.openai.audio.speech.SpeechModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class AppConfiguration {

  @Bean
  @Primary
  SpeechModel speechModel(@Value("${spring.ai.openai.api-key}") String apiKey) {
    return new OpenAiAudioSpeechModel(new OpenAiAudioApi(apiKey));
  }

  @Bean
  @Primary
  ImageModel imageModel(@Value("${spring.ai.openai.api-key}") String apiKey) {
    return new OpenAiImageModel(new OpenAiImageApi(apiKey));
  }

  @Bean
  @Primary
  ChatModel chatModel(@Value("${spring.ai.openai.api-key}") String apiKey) {
    return new OpenAiChatModel(new OpenAiApi(apiKey));
  }
}
