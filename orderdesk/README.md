# OrderDesk: a Spring AI demo app

Source code for the article series [Spring AI Tutorial](https://howtodoinjava.com/series/spring-ai-tutorial/).

OrderDesk is the support assistant of a demo online store. The Spring AI tutorials on
HowToDoInJava build it step by step; each tutorial adds one feature to this same app.

## Requirements

- Java 21 or newer, Maven 3.9 or newer
- Ollama running on this computer, with two models:

```bash
ollama pull qwen2.5:3b      # chat model
ollama pull qwen2.5vl:3b    # vision model, used only by the multimodal tutorial
ollama pull nomic-embed-text  # embedding model, used by the RAG tutorials
```

## Run

```bash
mvn spring-boot:run
```

## What each package shows

| Package | Tutorial | Try it |
|---|---|---|
| `chat` | [Getting Started with Spring AI](https://howtodoinjava.com/spring-ai/getting-started/), [Spring AI ChatClient API](https://howtodoinjava.com/spring-ai/spring-ai-chatclient/) | `curl "localhost:8080/ask?question=Do%20you%20deliver%20to%20Pune%3F"` |
| `prompts` | [Spring AI Prompts](https://howtodoinjava.com/spring-ai/prompt-template-example/) | `curl "localhost:8080/prompts/reply?customer=Priya&orderId=ORD-1042&complaint=late"` |
| `structured` | [Spring AI Structured Output](https://howtodoinjava.com/spring-ai/structured-output-converters/) | `curl "localhost:8080/structured/triage?message=My%20parcel%20is%20late"` |
| `memory` | [Spring AI Chat Memory](https://howtodoinjava.com/spring-ai/spring-ai-chat-memory/) | `curl -X POST localhost:8080/chat/priya -H "Content-Type: text/plain" -d "My order is ORD-1042"` |
| `streaming` | [Spring AI Streaming with SSE](https://howtodoinjava.com/spring-ai/spring-ai-streaming-sse/) | open `http://localhost:8080/chat.html` |
| `vision` | [Spring AI Multimodality](https://howtodoinjava.com/spring-ai/spring-ai-multimodality/) | `curl -F "photo=@src/main/resources/images/damaged-parcel.jpg;type=image/jpeg" localhost:8080/damage/report` |
| `moderation` | [Spring AI Moderation](https://howtodoinjava.com/spring-ai/spring-ai-moderation/) | `curl "localhost:8080/moderation/check?text=Where%20is%20my%20order%3F"` |
| `embedding` | [Spring AI EmbeddingModel](https://howtodoinjava.com/spring-ai/vector-embedding-example/) | `curl "localhost:8080/similar?question=Can%20I%20pay%20cash%3F"` |
| `etl` | [Spring AI ETL Pipeline](https://howtodoinjava.com/spring-ai/etl-pipeline-example/) | `curl -X POST localhost:8080/ingest` (loads `src/main/resources/policies`) |
| `vectorstore` | [Spring AI VectorStore](https://howtodoinjava.com/spring-ai/vector-store-example/), [PgVector](https://howtodoinjava.com/spring-ai/spring-ai-pgvectorstore-example/), [Redis](https://howtodoinjava.com/spring-ai/spring-ai-redis-vector-store/), [Vector Stores Compared](https://howtodoinjava.com/spring-ai/spring-ai-vector-stores-compared/) | `curl "localhost:8080/search?query=return%20a%20jacket&topic=returns"` |
| `rag` | [Spring AI RAG Advisors](https://howtodoinjava.com/spring-ai/spring-ai-rag-advisors/) | `curl "localhost:8080/rag/qa?question=How%20many%20days%20do%20I%20have%20to%20return%20a%20jacket%3F"` |
| `upload` | [Chat with Uploaded Files](https://howtodoinjava.com/spring-ai/chatbot-example-with-rag/) | open `http://localhost:8080/files.html` and upload `samples/brewmate-kt200-manual.pdf` |
| `tools` | [Spring AI Tool Calling](https://howtodoinjava.com/spring-ai/spring-ai-function-calling-example/) | `curl "localhost:8080/tools/ask?question=What%20is%20the%20status%20of%20order%20ORD-1039%3F"` |
| `advisors` | [Spring AI Advisors Explained](https://howtodoinjava.com/spring-ai/spring-ai-advisors/) | `curl "localhost:8080/advisors/ask?customerId=ravi&question=How%20long%20can%20I%20return%20a%20jacket%3F"` |
| `mcp` | [Spring AI MCP Server and Client](https://howtodoinjava.com/spring-ai/spring-ai-mcp/) | MCP endpoint `http://localhost:8080/mcp`; client app in `../orderdesk-mcp-client` |
| `agents` | [Spring AI Agent Workflow Patterns](https://howtodoinjava.com/spring-ai/spring-ai-agents/) | `curl "localhost:8080/agents/route?question=Has%20order%20ORD-1040%20been%20delivered%3F"` |
| `sql` | [Query a Database with AI](https://howtodoinjava.com/spring-ai/generate-sql-with-ai/) | `curl "localhost:8080/orders/ask?question=How%20many%20orders%20were%20delivered%20to%20Pune%3F"` |

## Vector stores

The default vector store is `SimpleVectorStore`: it runs in memory and is saved to
`data/vector-store.json`, so nothing else is needed. To use another store, start it in Docker
and run the app with its profile, for example:

```bash
docker run -d --name chroma -p 8000:8000 chromadb/chroma
mvn spring-boot:run -Dspring-boot.run.profiles=chroma
```

| Profile | Docker command |
|---|---|
| `chroma` | `docker run -d --name chroma -p 8000:8000 chromadb/chroma` |
| `pgvector` | `docker run -d --name pgvector -p 5432:5432 -e POSTGRES_USER=orderdesk -e POSTGRES_PASSWORD=orderdesk -e POSTGRES_DB=orderdesk pgvector/pgvector:pg17` |
| `redis` | `docker run -d --name redis-stack -p 6379:6379 redis/redis-stack-server` |
| `qdrant` | `docker run -d --name qdrant -p 6333:6333 -p 6334:6334 qdrant/qdrant` |

After switching stores, call `POST /ingest` once to load the policy files into the new store.

Chat memory and the `orders` table are stored in an HSQLDB file database in the `data` folder
(with the `pgvector` profile they move to PostgreSQL).
The parcel photo in `src/main/resources/images` is a drawn test image.

## Tests

Article: [Testing Spring AI Apps](https://howtodoinjava.com/spring-ai/spring-ai-testing/)


- `mvn test` runs the unit tests (no model needed).
- `mvn test -Dtest=SupportClientIT` starts Ollama in Docker with Testcontainers (the first run pulls `qwen2.5:0.5b` and saves the image as `orderdesk-ollama:qwen2.5-0.5b`).
- `mvn test -Dtest=RagAnswerEvaluationIT` judges RAG answers with the local Ollama (`qwen2.5:3b`).

## Observability

Article: [Spring AI Observability](https://howtodoinjava.com/spring-ai/spring-ai-observability/)


Metrics: `curl localhost:8080/actuator/metrics/gen_ai.client.token.usage`.
Traces: `docker run -d --name zipkin -p 9411:9411 openzipkin/zipkin`, then run with the `observability` profile and open http://localhost:9411.
