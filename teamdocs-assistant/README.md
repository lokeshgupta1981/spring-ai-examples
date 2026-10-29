Source code for the article [Building a Knowledge Assistant with Spring AI](https://howtodoinjava.com/spring-ai/ai-knowledge-assistant-spring-ai/) (draft).

Java 21 target, tested with JDK 25. Spring Boot 4.1.2 and Spring AI 2.0.2.

Run `mvn test` for four controlled tests. They use the real SimpleVectorStore with deterministic test vectors and a mocked ChatModel. They do not measure semantic retrieval or model answer quality.

For the application, start Ollama and download `qwen2.5:3b` and `nomic-embed-text` with `ollama pull`. Run `mvn spring-boot:run` from this directory. The application loads two bundled Markdown files, asks the configured question and prints JSON containing text and retrieved source passages. The live Ollama flow has not been run during controlled test verification.

Override the question with `mvn spring-boot:run -Dspring-boot.run.arguments="--app.question=What is on the cafeteria menu?"`.

The store is in memory and is rebuilt on startup. The configured team is server-owned demo configuration, not a login system. A web application must derive permitted documents from authenticated identity. Document replacement deletes then adds chunks and is not an atomic production revision mechanism. Returned sources are passages supplied to the model, not independently verified sentence-level citations.
