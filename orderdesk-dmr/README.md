# OrderDesk with Docker Model Runner

Source code for the article [Spring AI with Docker Model Runner](https://howtodoinjava.com/spring-ai/spring-ai-docker-model-runner/).

Versions: Java 21, Spring Boot 4.1.1, Spring AI 2.0.1. The app keeps the local Ollama model (`qwen2.5:3b`) next to the provider's model, so
Ollama must be running too.

## Run

Enable Docker Model Runner with TCP access on port 12434 and pull the model, then start the app:

```bash
docker model pull ai/gemma3
mvn spring-boot:run
```

The app starts on port 8087. Endpoints: `GET /ask`.

```bash
curl "localhost:8087/ask?question=Do%20you%20deliver%20to%20Pune%3F"
```
