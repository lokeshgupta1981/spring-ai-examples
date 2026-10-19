# OrderDesk: a Spring AI demo app

OrderDesk is the support assistant of a demo online store. The Spring AI tutorials on
HowToDoInJava build it step by step; each tutorial adds one feature to this same app.

## Requirements

- Java 21 or newer, Maven 3.9 or newer
- Ollama running on this computer, with two models:

```bash
ollama pull qwen2.5:3b      # chat model
ollama pull qwen2.5vl:3b    # vision model, used only by the multimodal tutorial
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

Chat memory is stored in an HSQLDB file database in the `data` folder.
The parcel photo in `src/main/resources/images` is a drawn test image.
