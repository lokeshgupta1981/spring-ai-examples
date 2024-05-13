package com.howtodoinjava.ai.demo;

import org.springframework.ai.chat.Generation;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.openai.OpenAiChatClient;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;

import java.util.List;
import java.util.Map;

@SpringBootApplication
public class PromptTemplateApplication {

  public static void main(String[] args) {
    new SpringApplicationBuilder(PromptTemplateApplication.class)
        .web(WebApplicationType.NONE)
        .run(args);
  }

  @Bean
  ApplicationRunner applicationRunner(OpenAiChatClient chatClient) {
    return args -> {
      userPrompt(chatClient);
    };
  }

  private void userPrompt(OpenAiChatClient chatClient) {

    String message = """
        Provide me a List of {subject}
        """;

    PromptTemplate promptTemplate = new PromptTemplate(message,
        Map.of("subject", "10 countries with largest population in descending oder"));
    Prompt prompt = new Prompt(promptTemplate.createMessage());

    Generation generation = chatClient.call(prompt).getResult();
    System.out.println(generation.getOutput().getContent());
  }

  private void systemAndUserPrompt(OpenAiChatClient chatClient) {

    //1. User provided input message
    String userText = """
        Tell me about five most famous tourist spots in Dubai(UAE).
        Write at least a sentence for each spot.
        """;

    Message userMessage = new UserMessage(userText);

    //2. Application defined system message
    String systemText = """
        You are a helpful AI assistant that helps people find information.
        Your name is {name}
        Start with telling your name and quick summary of answer you are going to provide in a sentence.
        Next, you should reply to the user's request. 
        Finish with thanking the user for asking question in the end.
        """;

    SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate(systemText);
    Message systemMessage = systemPromptTemplate.createMessage(Map.of("name", "Alexa"));

    //3. Prompt message containing user and system messages
    Prompt prompt = new Prompt(List.of(userMessage, systemMessage));

    //4. API invocation and result extraction
    Generation generation = chatClient.call(prompt).getResult();
    System.out.println(generation.getOutput().getContent());
  }
}
