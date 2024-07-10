package com.howtodoinjava.ai.demo;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.converter.ListOutputConverter;
import org.springframework.ai.converter.MapOutputConverter;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
class ChatService {

  private final ChatClient chatClient;

  ChatService(ChatClient.Builder chatClientBuilder) {
    this.chatClient = chatClientBuilder.build();
  }

  PlayerInfo chatWithBeanOutput(Question question) {
    var userPromptTemplate = """
      Tell me name and team of one player famous for playing the {sportName}.
      Consider only the players that play for the {country}.
      """;

    return chatClient.prompt()
      .user(userSpec -> userSpec
        .text(userPromptTemplate)
        .param("sportName", question.sportName())
        .param("country", question.country())
      )
      .options(OpenAiChatOptions.builder()
        .withResponseFormat(new OpenAiApi.ChatCompletionRequest.ResponseFormat("json_object"))
        .build())
      .call()
      .entity(PlayerInfo.class);
  }

  Map<String, Object> chatWithMapOutput(Question question) {
    var outputConverter = new MapOutputConverter();

    var userPromptTemplate = """
      Tell me the name of three players famous for playing the {sportName}.
      Consider only the players that play for the {country}.
      {format}
      """;

    var result = chatClient.prompt()
      .user(userSpec -> userSpec
        .text(userPromptTemplate)
        .param("sportName", question.sportName())
        .param("country", question.country())
        .param("format", outputConverter.getFormat())
      )
      .call()
      .content();
    return outputConverter.convert(result);
  }

  List<String> chatWithListOutput(Question question) {
    var outputConverter = new ListOutputConverter(new DefaultConversionService());

    var userPromptTemplate = """
      Tell me the name of three players famous for playing the {sportName}.
       Consider only the players that play for the {country}.
       {format}
       """;

    var result = chatClient.prompt()
      .user(userSpec -> userSpec
        .text(userPromptTemplate)
        .param("sportName", question.sportName())
        .param("country", question.country())
        .param("format", outputConverter.getFormat())
      )
      .call()
      .content();
    return outputConverter.convert(result);
  }
}
