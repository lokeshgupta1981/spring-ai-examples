package com.howtodoinjava.teamdocs;

import java.util.List;
import java.util.Locale;
import java.util.stream.IntStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.Embedding;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class KnowledgeAssistantTest {
    SimpleVectorStore store;
    KnowledgeIndex index;
    KnowledgeAssistant assistant;
    ChatModel model;

    @BeforeEach
    void setup() {
        store = SimpleVectorStore.builder(new TestEmbeddingModel()).build();
        index = new KnowledgeIndex(store);
        model = mock(ChatModel.class);
        when(model.getOptions()).thenReturn(ChatOptions.builder().build());
        assistant = new KnowledgeAssistant(store, ChatClient.builder(model));
    }

    @Test
    void replacingDocumentRemovesOldText() {
        index.replace("engineering", "vpn.md", "VPN access uses the old registration portal.");
        index.replace("engineering", "vpn.md", "VPN access now requires the new company portal.");
        var found = store.similaritySearch(SearchRequest.builder().query("VPN access").topK(10).build());
        assertThat(found).hasSize(1); // Re-ingestion does not duplicate this source.
        assertThat(found.get(0).getText()).contains("new company portal").doesNotContain("old");
    }

    @Test
    void onlyTrustedTeamDocumentsReachTheModel() {
        index.replace("engineering", "engineering-vpn.md", "VPN access requires device enrollment.");
        index.replace("finance", "finance-vpn.md", "VPN access requires finance approval.");
        when(model.call(any(Prompt.class))).thenReturn(new ChatResponse(List.of(
            new Generation(new AssistantMessage("Enroll the device first.")))));
        var result = assistant.ask("How do I get VPN access?", "engineering");
        var prompt = ArgumentCaptor.forClass(Prompt.class);
        verify(model).call(prompt.capture());
        assertThat(prompt.getValue().getContents()).contains("device enrollment")
            .doesNotContain("finance approval"); // Filtering happens before generation.
        assertThat(result.sources()).extracting(KnowledgeAssistant.Evidence::source)
            .containsExactly("engineering-vpn.md"); // Evidence is built from retrieved metadata.
    }

    @Test
    void noMatchingEvidenceSkipsGeneration() {
        index.replace("engineering", "vpn.md", "VPN access requires device enrollment.");
        var result = assistant.ask("What is on the cafeteria menu?", "engineering");
        assertThat(result.sources()).isEmpty(); // No chunk passed the similarity threshold.
        assertThat(result.text()).contains("could not find");
        verify(model, never()).call(any(Prompt.class)); // The application itself refuses to guess.
    }

    @Test
    void anotherTeamHasNoAccessToTheChunk() {
        index.replace("finance", "vpn.md", "VPN access requires finance approval.");
        var result = assistant.ask("How do I get VPN access?", "engineering");
        assertThat(result.sources()).isEmpty(); // A close vector is still excluded by team metadata.
        verify(model, never()).call(any(Prompt.class));
    }

    static class TestEmbeddingModel implements EmbeddingModel {
        private float[] vector(String text) {
            return text.toLowerCase(Locale.ROOT).contains("vpn")
                ? new float[]{1, 0} : new float[]{0, 1};
        }
        @Override public float[] embed(Document document) { return vector(document.getText()); }
        @Override public EmbeddingResponse call(EmbeddingRequest request) {
            return new EmbeddingResponse(IntStream.range(0, request.getInstructions().size())
                .mapToObj(i -> new Embedding(vector(request.getInstructions().get(i)), i)).toList());
        }
    }
}
