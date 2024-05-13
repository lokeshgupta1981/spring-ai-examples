package com.howtodoinjava.ai.demo.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.github.victools.jsonschema.generator.SchemaGenerator;
import com.github.victools.jsonschema.generator.SchemaGeneratorConfig;
import com.github.victools.jsonschema.generator.SchemaGeneratorConfigBuilder;
import com.github.victools.jsonschema.module.jackson.JacksonModule;
import com.howtodoinjava.ai.demo.model.Pair;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.converter.StructuredOutputConverter;
import org.springframework.lang.NonNull;

import java.util.Map;
import java.util.Objects;

import static com.github.victools.jsonschema.generator.OptionPreset.PLAIN_JSON;
import static com.github.victools.jsonschema.generator.SchemaVersion.DRAFT_2020_12;

public class CustomBeanOutputConverter<T> implements StructuredOutputConverter<T> {

  /** Holds the generated JSON schema for the target type. */
  private String jsonSchema;

  /** The Java class representing the target type. */
  @SuppressWarnings({ "FieldMayBeFinal", "rawtypes" })
  private Class<T> clazz;

  /** The object mapper used for deserialization and other JSON operations. */
  @SuppressWarnings("FieldMayBeFinal")
  private ObjectMapper objectMapper;

  /**
   * Constructor to initialize with the target type's class.
   * @param clazz The target type's class.
   */
  public CustomBeanOutputConverter(Class<T> clazz) {
    this(clazz, null);
  }

  /**
   * Constructor to initialize with the target type's class, a custom object mapper, and
   * a line endings normalizer to ensure consistent line endings on any platform.
   * @param clazz The target type's class.
   * @param objectMapper Custom object mapper for JSON operations. endings.
   */
  public CustomBeanOutputConverter(Class<T> clazz, ObjectMapper objectMapper) {
    Objects.requireNonNull(clazz, "Java Class cannot be null;");
    this.clazz = clazz;
    this.objectMapper = objectMapper != null ? objectMapper : getObjectMapper();
    generateSchema();
  }

  /**
   * Generates the JSON schema for the target type.
   */
  private void generateSchema() {
    JacksonModule jacksonModule = new JacksonModule();
    SchemaGeneratorConfigBuilder configBuilder = new SchemaGeneratorConfigBuilder(DRAFT_2020_12, PLAIN_JSON)
        .with(jacksonModule);
    SchemaGeneratorConfig config = configBuilder.build();
    SchemaGenerator generator = new SchemaGenerator(config);
    JsonNode jsonNode = generator.generateSchema(this.clazz);
    ObjectWriter objectWriter = new ObjectMapper().writer(new DefaultPrettyPrinter()
        .withObjectIndenter(new DefaultIndenter().withLinefeed(System.lineSeparator())));
    try {
      this.jsonSchema = objectWriter.writeValueAsString(jsonNode);
    }
    catch (JsonProcessingException e) {
      throw new RuntimeException("Could not pretty print json schema for " + this.clazz, e);
    }
  }

  @Override
  /**
   * Parses the given text to transform it to the desired target type.
   * @param text The LLM output in string format.
   * @return The parsed output in the desired target type.
   */
  public T convert(@NonNull String text) {
    try {
      // If the response is a JSON Schema, extract the properties and use them as
      // the response.
      text = this.jsonSchemaToInstance(text);
      return (T) this.objectMapper.readValue(text, this.clazz);
    }
    catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Converts a JSON Schema to an instance based on a given text.
   * @param text The JSON Schema in string format.
   * @return The JSON instance generated from the JSON Schema, or the original text if
   * the input is not a JSON Schema.
   */
  private String jsonSchemaToInstance(String text) {
    try {
      Map<String, Object> map = this.objectMapper.readValue(text, Map.class);
      if (map.containsKey("$schema")) {
        return this.objectMapper.writeValueAsString(map.get("properties"));
      }
    }
    catch (Exception e) {
    }
    return text;
  }

  /**
   * Configures and returns an object mapper for JSON operations.
   * @return Configured object mapper.
   */
  protected ObjectMapper getObjectMapper() {
    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    return mapper;
  }

  /**
   * Provides the expected format of the response, instructing that it should adhere to
   * the generated JSON schema.
   * @return The instruction format string.
   */
  @Override
  public String getFormat() {
    String template = """
				Your response should be in JSON format.
				Do not include any explanations, only provide a RFC8259 compliant JSON response following this format without deviation.
				Do not include markdown code blocks in your response.
				Remove the ```json markdown from the output.
				Here is the JSON Schema instance your output must adhere to:
				```%s```
				""";
    return String.format(template, this.jsonSchema);
  }
}
