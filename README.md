# Getting Started with Spring AI

## Prerequisites

Create an account at OpenAI Signup and generate the security token at [API Keys](https://platform.openai.com/api-keys).

Spring AI (for OpenAI integration) expects a configuration property named 'spring.ai.openai.api-key' that we should set to the value of the API Key obtained in teh above step.

```
export SPRING_AI_OPENAI_API_KEY=<INSERT KEY HERE>
```

## Run the Project

```
./mvnw spring-boot:run
```

## Related Tutorials

Refer to these tutorials for how to execute and what to expect in API calls.

* [Spring AI Tutorial (with Examples)](https://howtodoinjava.com/spring-ai/spring-ai-tutorial/)
* [Spring AI Example: Generate Image from Text](https://howtodoinjava.com/spring-ai/spring-ai-image-generation-example/)
* [Spring AI Structured Output Converters (List, Map and Bean)](https://howtodoinjava.com/spring-ai/structured-output-converters/)