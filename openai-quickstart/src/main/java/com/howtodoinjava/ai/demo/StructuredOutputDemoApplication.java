package com.howtodoinjava.ai.demo;

import java.util.List;
import java.util.Map;

import org.springframework.ai.chat.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.converter.ListOutputConverter;
import org.springframework.ai.converter.MapOutputConverter;
import org.springframework.ai.openai.OpenAiChatClient;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.support.DefaultConversionService;

@SpringBootApplication
public class StructuredOutputDemoApplication {

  //@Bean
  ApplicationRunner applicationRunner(OpenAiChatClient chatClient) {
    return args -> {
      listOutputConverter(chatClient);
      mapOutputConverter(chatClient);
      beanOutputConverter(chatClient);
    };
  }

  public void mapOutputConverter(OpenAiChatClient chatClient) {
    MapOutputConverter mapOutputConverter = new MapOutputConverter();

    String format = mapOutputConverter.getFormat();
    String template = """
        Provide me a List of {subject}
        {format}
        """;
    PromptTemplate promptTemplate = new PromptTemplate(template,
        Map.of("subject", "an array of numbers from 1 to 9 under they key name 'numbers'", "format", format));
    Prompt prompt = new Prompt(promptTemplate.createMessage());
    Generation generation = chatClient.call(prompt).getResult();

    Map<String, Object> result = mapOutputConverter.convert(generation.getOutput().getContent());

    System.out.println(result);
  }

  public void listOutputConverter(OpenAiChatClient chatClient) {
      ListOutputConverter listOutputConverter = new ListOutputConverter(new DefaultConversionService());

    String format = listOutputConverter.getFormat();
    String template = """
        List five {subject}
        {format}
        """;
    PromptTemplate promptTemplate = new PromptTemplate(template,
        Map.of("subject", "ice cream flavors", "format", format));
    Prompt prompt = new Prompt(promptTemplate.createMessage());
    Generation generation = chatClient.call(prompt).getResult();

    List<String> list = listOutputConverter.convert(generation.getOutput().getContent());
    System.out.println(list);
  }

  public void beanOutputConverter(OpenAiChatClient chatClient) {

    record ActorsFilms(String actor, List<String> movies) {
    }

    BeanOutputConverter<ActorsFilms> beanOutputConverter = new BeanOutputConverter<>(ActorsFilms.class);

    String format = beanOutputConverter.getFormat();

    String actor = "Tom Hanks";

    String template = """
        Generate the filmography of 5 movies for {actor}.
        {format}
        """;

    Generation generation = chatClient.call(
            new Prompt(new PromptTemplate(template, Map.of("actor", actor, "format", format)).createMessage()))
        .getResult();

    ActorsFilms actorsFilms = beanOutputConverter.convert(generation.getOutput().getContent());

    System.out.println(actorsFilms);
  }

  public static void main(String[] args) {
    new SpringApplicationBuilder(StructuredOutputDemoApplication.class)
        .web(WebApplicationType.NONE)
        .run(args);
  }
}
