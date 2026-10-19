# OrderDesk: a Spring AI demo app

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
| `chat` | Spring AI ChatClient API | `curl "localhost:8080/ask?question=Do%20you%20deliver%20to%20Pune%3F"` |
| `memory` | Spring AI Chat Memory | `curl -X POST localhost:8080/chat/priya -H "Content-Type: text/plain" -d "My order is ORD-1042"` |
| `streaming` | Spring AI Streaming with SSE | open `http://localhost:8080/chat.html` |
| `vision` | Spring AI Multimodality | `curl -F "photo=@src/main/resources/images/damaged-parcel.jpg;type=image/jpeg" localhost:8080/damage/report` |
| `embedding` | Spring AI EmbeddingModel | `curl "localhost:8080/similar?question=Can%20I%20pay%20cash%3F"` |
| `etl` | Spring AI ETL Pipeline | `curl -X POST localhost:8080/ingest` (loads `src/main/resources/policies`) |
| `vectorstore` | Spring AI VectorStore, PgVector, Redis, Vector Stores Compared | `curl "localhost:8080/search?query=return%20a%20jacket&topic=returns"` |
| `rag` | Spring AI RAG Advisors | `curl "localhost:8080/rag/qa?question=How%20many%20days%20do%20I%20have%20to%20return%20a%20jacket%3F"` |
| `upload` | Chat with Uploaded Files | open `http://localhost:8080/files.html` and upload `samples/brewmate-kt200-manual.pdf` |
| `sql` | Query a Database with AI | `curl "localhost:8080/orders/ask?question=How%20many%20orders%20were%20delivered%20to%20Pune%3F"` |

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
