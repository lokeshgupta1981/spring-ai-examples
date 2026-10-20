# OrderDesk with Amazon Bedrock Converse

Source code for the article [Spring AI with Amazon Bedrock Converse](https://howtodoinjava.com/spring-ai/spring-ai-bedrock/).

Versions: Java 21, Spring Boot 4.1.1, Spring AI 2.0.1. The app keeps the local Ollama model (`qwen2.5:3b`) next to the provider's model, so
Ollama must be running too.

## Run

AWS credentials come from the default AWS chain: the environment variables
`AWS_ACCESS_KEY_ID` and `AWS_SECRET_ACCESS_KEY`, or `~/.aws/credentials`. The region is `us-east-1`.

```bash
mvn spring-boot:run
```

The app starts on port 8086. Endpoints: `GET /ask`.

```bash
curl "localhost:8086/ask?question=Do%20you%20deliver%20to%20Pune%3F"
```
