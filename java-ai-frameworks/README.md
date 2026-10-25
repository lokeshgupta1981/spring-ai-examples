Source code for the article [Spring AI vs LangChain4j vs Embabel vs AgentScope Java](https://howtodoinjava.com/ai/java-ai-frameworks-compared/)

The same support-desk tool call (`getTicketStatus`) written in four Java AI frameworks. Each module is a separate Maven project:

| Module | Framework | Java |
|---|---|---|
| `spring-ai` | Spring AI 2.0.2 on Spring Boot 4.1.2 | 17 |
| `langchain4j` | LangChain4j 1.22.0 | 17 |
| `embabel` | Embabel 1.5.3 on Spring Boot 4.1.2 | 21 |
| `agentscope` | AgentScope Java 2.0.4 | 17 |

All modules use a local Ollama model (`qwen2.5:7b`). Start Ollama and download the model first:

```bash
ollama pull qwen2.5:7b
```

Build all modules with Maven 3.9 and JDK 21:

```bash
mvn compile
```

The modules compile against the versions above; they show each framework's programming style and were not run for the article.
