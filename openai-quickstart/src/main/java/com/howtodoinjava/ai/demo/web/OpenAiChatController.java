package com.howtodoinjava.ai.demo.web;

import com.howtodoinjava.ai.demo.model.JokeResponse;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class OpenAiChatController {

  private final ChatClient chatClient;
  private String promptTemplate;
  private String jsonPromptTemplate;

  @Autowired
  public OpenAiChatController(ChatClient chatClient,
                          @Value("${app.joke.simple.promptTemplate}") String promptTemplate,
                          @Value("${app.joke.formatted.promptTemplate}") String jsonPromptTemplate) {
    this.chatClient = chatClient;
    this.promptTemplate = promptTemplate;
    this.jsonPromptTemplate =jsonPromptTemplate;
  }

  @GetMapping("/joke-service/simple")
  public Map<String, String> tellSimpleJoke() {
    return Map.of("generation", chatClient.call("Tell me a joke"));
  }

  @GetMapping("/joke-service/simple-with-prompt")
  public String tellSimpleJokeWithPrompt(@RequestParam("subject") String subject,
                                         @RequestParam("language") String language) {
    PromptTemplate pt = new PromptTemplate(promptTemplate);
    Prompt renderedPrompt = pt.create(Map.of("subject", subject, "language", language));

    ChatResponse response = chatClient.call(renderedPrompt);
    return response.getResult().getOutput().getContent();
  }

  @GetMapping("/joke-service/json-with-prompt")
  public JokeResponse tellSpecificJokeInJsonFormat(@RequestParam("subject") String subject,
                                       @RequestParam("language") String language) {

    BeanOutputConverter<JokeResponse> parser = new BeanOutputConverter<>(JokeResponse.class);
    /**
     * Your response should be in JSON format.
     * Do not include any explanations, only provide a RFC8259 compliant JSON response following this format without deviation.
     * Do not include markdown code blocks in your response.
     * Remove the ```json markdown from the output.
     * Here is the JSON Schema instance your output must adhere to:
     * ```{
     *   "$schema" : "https://json-schema.org/draft/2020-12/schema",
     *   "type" : "object",
     *   "properties" : {
     *     "joke" : {
     *       "type" : "string"
     *     },
     *     "language" : {
     *       "type" : "string"
     *     },
     *     "subject" : {
     *       "type" : "string"
     *     }
     *   }
     * }```
     * */
    String format = parser.getFormat();

    PromptTemplate pt = new PromptTemplate(jsonPromptTemplate);
    Prompt renderedPrompt = pt.create(Map.of("subject", subject, "language", language, "format", format));

    ChatResponse response = chatClient.call(renderedPrompt);

    /*Usage usage = response.getMetadata().getUsage();
    System.out.println("Usage: " + usage.getPromptTokens() + " " + usage.getGenerationTokens() + "; " + usage.getTotalTokens());*/

    return parser.parse(response.getResult().getOutput().getContent());
  }
}
