package com.howtodoinjava.ai.demo.web;

import com.howtodoinjava.ai.demo.model.JokeResponse;
import com.howtodoinjava.ai.demo.model.Pair;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.converter.ListOutputConverter;
import org.springframework.ai.converter.MapOutputConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class OpenAiChatController {

  private final ChatModel chatModel;
  private String promptTemplate;
  private String jsonPromptTemplate;

  @Autowired
  public OpenAiChatController(ChatModel chatModel,
                          @Value("${app.joke.simple.promptTemplate}") String promptTemplate,
                          @Value("${app.joke.formatted.promptTemplate}") String jsonPromptTemplate) {
    this.chatModel = chatModel;
    this.promptTemplate = promptTemplate;
    this.jsonPromptTemplate =jsonPromptTemplate;
  }

  @GetMapping("/joke-service/simple")
  public Map<String, String> tellSimpleJoke() {
    return Map.of("generation", chatModel.call("Tell me a joke"));
  }

  @GetMapping("/joke-service/simple-with-prompt")
  public String tellSimpleJokeWithPrompt(@RequestParam("subject") String subject,
                                         @RequestParam("language") String language) {
    PromptTemplate pt = new PromptTemplate(promptTemplate);
    Prompt renderedPrompt = pt.create(Map.of("subject", subject, "language", language));

    ChatResponse response = chatModel.call(renderedPrompt);
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

    ChatResponse response = chatModel.call(renderedPrompt);

    /*Usage usage = response.getMetadata().getUsage();
    System.out.println("Usage: " + usage.getPromptTokens() + " " + usage.getGenerationTokens() + "; " + usage.getTotalTokens());*/

    return parser.parse(response.getResult().getOutput().getContent());
  }

  @GetMapping("/country-capital-service/map")
  public Map<String, Object> getCapitalNamesInMap(@RequestParam String countryNamesCsv) {
    if (countryNamesCsv == null || countryNamesCsv.isEmpty()) {
      throw new IllegalArgumentException("Country names CSV cannot be null or empty");
    }
    MapOutputConverter converter = new MapOutputConverter();
    String format = converter.getFormat();
    PromptTemplate pt = new PromptTemplate("For these list of countries {countryNamesCsv}, return the list of capitals. {format}");
    Prompt renderedPrompt = pt.create(Map.of("countryNamesCsv", countryNamesCsv, "format", format));
    ChatResponse response = chatModel.call(renderedPrompt);
    Generation generation = response.getResult();  // call getResults() if multiple generations
    System.out.println(generation.getOutput().getContent());
    return converter.parse(generation.getOutput().getContent());
  }

  @GetMapping("/country-capital-service/bean")
  public Pair getCapitalNamesInPojo(@RequestParam String countryName) {

    if (countryName == null || countryName.isEmpty()) {
      throw new IllegalArgumentException("Country names CSV cannot be null or empty");
    }

    BeanOutputConverter<Pair> converter = new BeanOutputConverter(Pair.class);
    String format = converter.getFormat();

    PromptTemplate pt = new PromptTemplate("For these list of countries {countryName}, return the list of its 10 popular cities. {format}");
    Prompt renderedPrompt = pt.create(Map.of("countryName", countryName, "format", format));

    ChatResponse response = chatModel.call(renderedPrompt);
    Generation generation = response.getResult();  // call getResults() if multiple generations
    return converter.parse(generation.getOutput().getContent());
  }

  @GetMapping("/country-capital-service/list")
  public List<String> getCapitalNamesInList(@RequestParam String countryNamesCsv) {
    if (countryNamesCsv == null || countryNamesCsv.isEmpty()) {
      throw new IllegalArgumentException("Country names CSV cannot be null or empty");
    }
    ListOutputConverter converter = new ListOutputConverter(new DefaultConversionService());
    String format = converter.getFormat();
    PromptTemplate pt = new PromptTemplate("For these list of countries {countryNamesCsv}, return the list of capitals. {format}");
    Prompt renderedPrompt = pt.create(Map.of("countryNamesCsv", countryNamesCsv, "format", format));
    ChatResponse response = chatModel.call(renderedPrompt);
    Generation generation = response.getResult();  // call getResults() if multiple generations
    System.out.println(generation.getOutput().getContent());
    return converter.parse(generation.getOutput().getContent());
  }
}
