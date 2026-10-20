# OrderDesk Migration: Spring AI 1.x to 2.0

Source code for the article [Spring AI 1.x to 2.0 Migration Guide](https://howtodoinjava.com/spring-ai/spring-ai-2-migration/).

The same small app twice:

| Folder | Versions |
|---|---|
| `before` | Spring Boot 3.5.15, Spring AI 1.0.9 |
| `after` | Spring Boot 4.1.1, Spring AI 2.0.1 |

Both run on a local Ollama with `qwen2.5:3b` and `nomic-embed-text`, and need Java 21.

## Run

```bash
cd before   # or: cd after
mvn spring-boot:run
curl "localhost:8080/ask?question=Do%20you%20deliver%20to%20Pune%3F"
```

Both apps expose `GET /ask`, `GET /ask/stream` and `GET /triage`.
