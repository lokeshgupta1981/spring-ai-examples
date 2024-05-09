package com.howtodoinjava.ai.demo.config;

import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.image.ImageClient;
import org.springframework.ai.openai.OpenAiAudioSpeechClient;
import org.springframework.ai.openai.OpenAiChatClient;
import org.springframework.ai.openai.OpenAiImageClient;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.openai.api.OpenAiAudioApi;
import org.springframework.ai.openai.api.OpenAiImageApi;
import org.springframework.ai.openai.audio.speech.SpeechClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfiguration {

  @Bean
  SpeechClient speechClient(@Value("${spring.ai.openai.api-key}") String apiKey) {
    return new OpenAiAudioSpeechClient(new OpenAiAudioApi(apiKey));
  }

  @Bean
  ImageClient imageClient(@Value("${spring.ai.openai.api-key}") String apiKey) {
    return new OpenAiImageClient(new OpenAiImageApi(apiKey));
  }

  @Bean
  ChatClient chatClient(@Value("${spring.ai.openai.api-key}") String apiKey) {
    return new OpenAiChatClient(new OpenAiApi(apiKey));
  }
}
