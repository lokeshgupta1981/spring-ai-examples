# Spring AI Examples

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

## OrderDesk (Spring AI 2.0)

The `orderdesk` folder is a separate Spring Boot 4.1 and Spring AI 2.0.1 app that runs on a local
Ollama model, with no API key. See [orderdesk/README.md](orderdesk/README.md). The other folders
use Spring AI 1.0 and OpenAI.
