Source code for the article [Getting Consistent LLM Responses with Spring AI](https://howtodoinjava.com/spring-ai/consistent-llm-responses/) (draft).

Java 21 target, tested with JDK 25. Spring Boot 4.1.2, Spring AI 2.0.2, MockWebServer 4.12.0.

Run `mvn test` for four controlled tests. No Ollama server is required.

For the live experiment, start Ollama and run `ollama pull qwen2.5:3b`, then `mvn spring-boot:run -Dspring-boot.run.arguments="--app.measure=true --app.runs=10"`.

Change one setting at a time, for example add `--app.temperature=0.7` inside the application arguments. Results are measured locally, not precomputed. The live experiment has not been run as part of controlled test verification. Failed calls count in both score denominators. The single-ticket runner is a debugging example, not a representative quality benchmark.
