# OrderDesk with DeepSeek

Source code for the article [Spring AI with DeepSeek](https://howtodoinjava.com/spring-ai/spring-ai-deepseek/).

Versions: Java 21, Spring Boot 4.1.1, Spring AI 2.0.1. The app keeps the local Ollama model (`qwen2.5:3b`) next to the provider's model, so
Ollama must be running too.

## Run

```bash
export SPRING_AI_DEEPSEEK_API_KEY=<your key>
mvn spring-boot:run
```

The app starts on port 8085. Endpoints: `GET /ask`, `GET /ask/reason`.

```bash
curl "localhost:8085/ask?question=Do%20you%20deliver%20to%20Pune%3F"
```
