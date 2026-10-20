# OrderDesk with Anthropic Claude

Source code for the article [Spring AI with Anthropic Claude](https://howtodoinjava.com/spring-ai/spring-ai-anthropic/).

Versions: Java 21, Spring Boot 4.1.1, Spring AI 2.0.1. The app keeps the local Ollama model (`qwen2.5:3b`) next to the provider's model, so
Ollama must be running too.

## Run

```bash
export SPRING_AI_ANTHROPIC_API_KEY=<your key>
mvn spring-boot:run
```

The app starts on port 8083. Endpoints: `GET /ask`, `GET /ask/think`.

```bash
curl "localhost:8083/ask?question=Do%20you%20deliver%20to%20Pune%3F"
```
