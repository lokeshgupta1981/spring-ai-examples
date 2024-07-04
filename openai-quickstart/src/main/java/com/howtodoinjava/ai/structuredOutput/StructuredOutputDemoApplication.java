package com.howtodoinjava.ai.structuredOutput;

import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.converter.ListOutputConverter;
import org.springframework.ai.converter.MapOutputConverter;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.core.convert.support.DefaultConversionService;

import java.util.List;
import java.util.Map;

@SpringBootApplication
public class StructuredOutputDemoApplication {

  //@Bean(name = "structuredOutputDemoApplicationRunner")
  ApplicationRunner applicationRunner(OpenAiChatModel chatModel) {
    return args -> {
      listOutputConverter(chatModel);
      mapOutputConverter(chatModel);
      beanOutputConverter(chatModel);
    };
  }

  public void mapOutputConverter(OpenAiChatModel chatModel) {
    MapOutputConverter mapOutputConverter = new MapOutputConverter();

    String format = mapOutputConverter.getFormat();
    String template = """
        Provide me a List of {subject}
        {format}
        """;
    PromptTemplate promptTemplate = new PromptTemplate(template,
        Map.of("subject", "an array of numbers from 1 to 9 under they key name 'numbers'", "format", format));
    Prompt prompt = new Prompt(promptTemplate.createMessage());
    Generation generation = chatModel.call(prompt).getResult();

    Map<String, Object> result = mapOutputConverter.convert(generation.getOutput().getContent());

    System.out.println(result);
  }

  public void listOutputConverter(OpenAiChatModel chatModel) {
      ListOutputConverter listOutputConverter = new ListOutputConverter(new DefaultConversionService());

    String format = listOutputConverter.getFormat();
    String template = """
        List five {subject}
        {format}
        """;
    PromptTemplate promptTemplate = new PromptTemplate(template,
        Map.of("subject", "ice cream flavors", "format", format));
    Prompt prompt = new Prompt(promptTemplate.createMessage());
    Generation generation = chatModel.call(prompt).getResult();

    List<String> list = listOutputConverter.convert(generation.getOutput().getContent());
    System.out.println(list);
  }

  public void beanOutputConverter(OpenAiChatModel chatModel) {

    record ActorsFilms(String actor, List<String> movies) {
    }

    BeanOutputConverter<ActorsFilms> beanOutputConverter = new BeanOutputConverter<>(ActorsFilms.class);

    String format = beanOutputConverter.getFormat();

    String actor = "Tom Hanks";

    String template = """
        Generate the filmography of 5 movies for {actor}.
        {format}
        """;

    Generation generation = chatModel.call(
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
