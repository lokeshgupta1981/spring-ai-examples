# Spring AI Examples

Source code for the [Spring AI Tutorial](https://howtodoinjava.com/series/spring-ai-tutorial/) on HowToDoInJava.

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
have their own dependencies and provider configuration; check each example's README before running it.

| Folder | Articles |
|---|---|
| [orderdesk](orderdesk) | Most of the series; the package table in its README links each article |
| [orderdesk-media](orderdesk-media) | Image generation, text to speech, speech to text, moderation |
| [orderdesk-mcp-client](orderdesk-mcp-client) | Spring AI MCP (client side) |
| [orderdesk-migration](orderdesk-migration) | Spring AI 1.x to 2.0 migration |
| [orderdesk-anthropic](orderdesk-anthropic), [orderdesk-gemini](orderdesk-gemini), [orderdesk-deepseek](orderdesk-deepseek), [orderdesk-bedrock](orderdesk-bedrock), [orderdesk-dmr](orderdesk-dmr) | One model provider each |
| [java-ai-frameworks](java-ai-frameworks) | Spring AI vs LangChain4j vs Embabel vs AgentScope Java (one tool call in each framework) |
| [multi-agent-ai-java](multi-agent-ai-java) | Multi-Agent AI in Java (LangChain4j agentic support desk, plain Java) |
| [spring-ai-error-handling](spring-ai-error-handling) | Standalone Spring AI request retries, timeouts, fallback, structured-output correction and streaming failures |
| [orderdesk-consistency](orderdesk-consistency) | Consistent classifications, native structured output and repeatability measurements |
| [teamdocs-assistant](teamdocs-assistant) | Knowledge assistant with filtered retrieval, source evidence and document replacement |
