package com.howtodoinjava.teamdocs;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.stereotype.Service;

@Service
public class KnowledgeIndex {
    private final VectorStore store;

    public KnowledgeIndex(VectorStore store) { this.store = store; }

    public void replace(String team, String source, String text) {
        if (team.isBlank() || source.isBlank() || text.isBlank()) {
            throw new IllegalArgumentException("Team, source and text are required");
        }
        var metadata = Map.<String, Object>of("team", team, "source", source);
        var chunks = TokenTextSplitter.builder().withChunkSize(300).build()
            .apply(List.of(new Document(text, metadata)));
        var indexed = new ArrayList<Document>();
        for (int i = 0; i < chunks.size(); i++) {
            String key = team + "\n" + source + "\n" + i;
            String id = UUID.nameUUIDFromBytes(key.getBytes(StandardCharsets.UTF_8)).toString();
            indexed.add(Document.builder().id(id).text(chunks.get(i).getText())
                .metadata(metadata).build());
        }
        var filter = new FilterExpressionBuilder();
        store.delete(filter.and(filter.eq("team", team), filter.eq("source", source)).build());
        store.add(indexed);
    }
}
