package com.howtodoinjava.ai.demo;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;

import java.util.List;
import java.util.Map;
import org.springframework.core.io.Resource;

@SpringBootApplication
public class PromptTemplateApplication {

  public static void main(String[] args) {
    new SpringApplicationBuilder(PromptTemplateApplication.class)
        .web(WebApplicationType.NONE)
        .run(args);
  }

  @Value("classpath:/prompts/system-message.st")
  private Resource promptSystemMessage;

  @Value("classpath:/prompts/user-message.st")
  private Resource promptUserMessage;

  @Value("classpath:/prompts/get-list-message.st")
  private Resource promptGetListMessage;

  @Bean(name = "promptTemplateApplicationRunner")
  ApplicationRunner applicationRunner(OpenAiChatModel chatModel) {
    return args -> {
      userPrompt(chatModel);
      systemAndUserPrompt(chatModel);
    };
  }

  private void userPrompt(OpenAiChatModel chatModel) {

    PromptTemplate promptTemplate = new PromptTemplate("Tell me about {subject}. Explain if I am {age} years old.");

    //Obtain these values from user
    String subject = "USA Elections";
    int age = 14;

    Prompt prompt = promptTemplate.create(Map.of("subject", subject, "age", age));
    Generation generation = chatModel
      .call(prompt)
      .getResult();

    System.out.println(prompt.getContents());
    System.out.println(generation.getOutput().getContent());
  }

  private void systemAndUserPrompt(OpenAiChatModel chatModel) {

    //1. Template from user message
    String userText = """
        Tell me about five most famous tourist spots in {location}.
        Write at least a sentence for each spot.
        """;

    String location = "Dubai(UAE)";  //Get from user
    PromptTemplate userPromptTemplate = new PromptTemplate(userText);
    Message userMessage = userPromptTemplate.createMessage(Map.of("location", location));

    //2. Template for system message
    String systemText = """
        You are a helpful AI assistant that helps people find information. Your name is {name}
        In your first response, greet the user, quick summary of answer and then do not repeat it. 
        Next, you should reply to the user's request. 
        Finish with thanking the user for asking question in the end.
        """;

    SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate(systemText);
    Message systemMessage = systemPromptTemplate.createMessage(Map.of("name", "Alexa"));

    //3. Prompt message containing user and system messages
    Prompt prompt = new Prompt(List.of(userMessage, systemMessage));

    //4. API invocation and result extraction
    Generation generation = chatModel.call(prompt).getResult();
    System.out.println(generation.getOutput());
  }
}
