package com.howtodoinjava.orderdesk.it;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.ollama.OllamaContainer;
import org.testcontainers.utility.DockerImageName;

// Starts Ollama in Docker, so the test needs no Ollama on the build machine
@SpringBootTest(properties = {
    "spring.ai.ollama.chat.options.model=" + SupportClientIT.MODEL,
    "spring.datasource.url=jdbc:hsqldb:mem:it",
    "spring.ai.mcp.server.enabled=false"})
@Testcontainers
class SupportClientIT {

    static final String MODEL = "qwen2.5:0.5b";
    // Ollama image with the model already inside, created on the first run
    static final String CACHED_IMAGE = "orderdesk-ollama:qwen2.5-0.5b";

    @Container
    @ServiceConnection
    static OllamaContainer ollama = new OllamaContainer(imageName());

    static DockerImageName imageName() {
        boolean cached = !DockerClientFactory.instance().client()
            .listImagesCmd().withImageNameFilter(CACHED_IMAGE).exec().isEmpty();
        return cached
            ? DockerImageName.parse(CACHED_IMAGE).asCompatibleSubstituteFor("ollama/ollama")
            : DockerImageName.parse("ollama/ollama:0.40.6");
    }

    @BeforeAll
    static void pullModelOnce() throws IOException, InterruptedException {
        if (ollama.getDockerImageName().equals(CACHED_IMAGE)) {
            return;
        }
        ollama.execInContainer("ollama", "pull", MODEL);
        ollama.commitToImage(CACHED_IMAGE);
    }

    @Autowired
    @Qualifier("supportClient")
    ChatClient supportClient;

    @Test
    void answersFromTheSupportRules() {
        String answer = supportClient.prompt()
            .user("Do you deliver to London?")
            .call()
            .content();

        System.out.println("Answer: " + answer);
        assertThat(answer).containsIgnoringCase("India");
    }
}
