package com.howtodoinjava.ai.demo;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.ChatOptionsBuilder;

@Service
public class TextClassifierService {

  private final ChatClient chatClient;

  TextClassifierService(ChatClient.Builder chatClientBuilder) {
    this.chatClient = chatClientBuilder
      .defaultOptions(ChatOptionsBuilder.builder()
        .withTemperature(0.0f)
        .build())
      .build();
  }

  String classifyEmotion(String text) {
    return chatClient
      .prompt()
      .system("""
        Classify the provided text into one of these classes:
        POSITIVE, NEGATIVE, NEUTRAL
        """)
      .user(text)
      .call()
      .content();
  }

  ClassificationType classifyEmotionStructured(String text) {
    return chatClient
      .prompt()
      .system("""
        Classify the provided text into one of these classes:
        POSITIVE, NEGATIVE, NEUTRAL
        """)
      .user(text)
      .call()
      .entity(ClassificationType.class);
  }

  String classifyEmotionWithHints(String text) {
    return chatClient
      .prompt()
      .system("""
        Classify the provided text into one of these classes.
                            
        POSITIVE: Happy, excellent, reliable, durable, efficient, innovative, intuitive, exceptional, high-quality, impressive, user-friendly.
        NEGATIVE: Unhappy, sad, Defective, unreliable, disappointing, frustrating, complicated, poor-quality
        NEUTRAL: OK, ordinary, plain, regular, standard, average, common, typical, normal, indifferent
        """)
      .user(text)
      .call()
      .content();
  }

  String classifyEmotionWithFewShotsPrompt(String text) {
    return chatClient
      .prompt()
      .system("""
        Classify the provided text into one of these classes.
                            
        POSITIVE: Happy, excellent, reliable, durable, efficient, innovative, intuitive, exceptional, high-quality, impressive, user-friendly.
        NEGATIVE: Unhappy, sad, Defective, unreliable, disappointing, frustrating, complicated, poor-quality
        NEUTRAL: OK, ordinary, plain, regular, standard, average, common, typical, normal, indifferent
                            
        ---
                            
        Text: The product is highly efficient and user-friendly.
        Class: POSITIVE
                
        Text: The service was disappointing and frustrating.
        Class: NEGATIVE
                
        Text: The performance is average, just as expected.
        Class: NEUTRAL
                
        Text: The software is intuitive and impressive.
        Class: POSITIVE
                
        Text: The device is defective and complicated to use.
        Class: NEGATIVE
                
        Text: The overall experience was plain and ordinary.
        Class: NEUTRAL
        """)
      .user(text)
      .call()
      .content();
  }

  String classifyEmotionWithFewShotsHistory(String text) {
    return chatClient
      .prompt()
      .messages(getPromptWithFewShotsHistory())
      .user(text)
      .call()
      .content();
  }

  private List<Message> getPromptWithFewShotsHistory() {
    return List.of(
      new SystemMessage("""
        Classify the provided text into one of these classes.
                            
        POSITIVE: Happy, excellent, reliable, durable, efficient, innovative, intuitive, exceptional, high-quality, impressive, user-friendly.
        NEGATIVE: Unhappy, sad, Defective, unreliable, disappointing, frustrating, complicated, poor-quality
        NEUTRAL: OK, ordinary, plain, regular, standard, average, common, typical, normal, indifferent
        """),

      new UserMessage("The product is highly efficient and user-friendly."),
      new AssistantMessage("POSITIVE"),

      new UserMessage("The service was disappointing and frustrating."),
      new AssistantMessage("NEGATIVE"),

      new UserMessage("The performance is average, just as expected."),
      new AssistantMessage("NEUTRAL"),

      new UserMessage("The software is intuitive and impressive."),
      new AssistantMessage("POSITIVE"),

      new UserMessage("The device is defective and complicated to use."),
      new AssistantMessage("NEGATIVE"),

      new UserMessage("The overall experience was plain and ordinary."),
      new AssistantMessage("NEUTRAL")
    );
  }
}
