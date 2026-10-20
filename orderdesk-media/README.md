# OrderDesk Media

Source code for the articles:

- [Spring AI ImageModel: Generate Images from Text](https://howtodoinjava.com/spring-ai/spring-ai-image-generation-example/)
- [Spring AI Text to Speech](https://howtodoinjava.com/spring-ai/tts-text-to-speech/)
- [Spring AI Speech to Text](https://howtodoinjava.com/spring-ai/transcription-speech-to-text/)
- [Spring AI Moderation](https://howtodoinjava.com/spring-ai/spring-ai-moderation/)

Versions: Java 21, Spring Boot 4.1.1, Spring AI 2.0.1. The app uses the OpenAI API, so it needs an API key and each request is billed.

## Run

```bash
export SPRING_AI_OPENAI_API_KEY=<your key>
mvn spring-boot:run
```

The app starts on port 8082.

## Endpoints

| Endpoint | Article |
|---|---|
| `GET /images/banner` | Image generation |
| `GET /speech`, `GET /speech/calm`, `GET /speech/stream` | Text to speech |
| `POST /transcribe`, `POST /voice-question` | Speech to text |
| `GET /moderate` | Moderation |
