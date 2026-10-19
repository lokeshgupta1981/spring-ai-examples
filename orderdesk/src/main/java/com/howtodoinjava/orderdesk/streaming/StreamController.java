package com.howtodoinjava.orderdesk.streaming;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Flux;

@RestController
public class StreamController {

    private static final Logger log = LoggerFactory.getLogger(StreamController.class);

    private final ChatClient supportClient;

    StreamController(@Qualifier("supportClient") ChatClient supportClient) {
        this.supportClient = supportClient;
    }

    // For EventSource in the browser, which can only send GET requests
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    Flux<String> stream(@RequestParam String question) {
        return supportClient.prompt()
            .user(question)
            .stream()
            .content();
    }

    // Each piece wrapped in JSON, so the browser keeps its leading space
    @GetMapping(value = "/stream-json", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    Flux<Map<String, String>> streamJson(@RequestParam String question) {
        return supportClient.prompt()
            .user(question)
            .stream()
            .content()
            .map(piece -> Map.of("text", piece))
            .doOnCancel(() -> log.info("Client disconnected, model call cancelled"));
    }

    // For fetch() in the browser, when the question is too long for a URL
    @PostMapping(value = "/stream-json", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    Flux<Map<String, String>> streamJsonPost(@RequestBody String question) {
        return streamJson(question);
    }
}
