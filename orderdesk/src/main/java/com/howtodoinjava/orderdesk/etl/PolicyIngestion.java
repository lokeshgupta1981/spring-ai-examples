package com.howtodoinjava.orderdesk.etl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.markdown.MarkdownDocumentReader;
import org.springframework.ai.reader.markdown.config.MarkdownDocumentReaderConfig;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

import com.howtodoinjava.orderdesk.vectorstore.SimpleVectorStoreConfig;

@Service
public class PolicyIngestion {

    private static final Logger log = LoggerFactory.getLogger(PolicyIngestion.class);

    private final VectorStore vectorStore;

    PolicyIngestion(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    public IngestResult ingest() throws IOException {
        long start = System.currentTimeMillis();

        // Extract: one Document per section of every policy file
        Resource[] files = new PathMatchingResourcePatternResolver()
            .getResources("classpath:policies/*.md");
        List<Document> documents = new ArrayList<>();
        for (Resource file : files) {
            String topic = file.getFilename().replace(".md", "");
            MarkdownDocumentReaderConfig config = MarkdownDocumentReaderConfig.builder()
                .withAdditionalMetadata("topic", topic)
                .withAdditionalMetadata("source", file.getFilename())
                .build();
            documents.addAll(new MarkdownDocumentReader(file, config).get());
        }

        // Transform: put each section's heading back into its text,
        // because MarkdownDocumentReader keeps the heading only in the metadata
        documents = documents.stream()
            .map(d -> new Document(d.getMetadata().get("title") + "\n" + d.getText(),
                d.getMetadata()))
            .toList();

        // Transform: split long sections into chunks the embedding model can take
        TokenTextSplitter splitter = TokenTextSplitter.builder()
            .withChunkSize(300)
            .build();
        List<Document> chunks = splitter.apply(documents);

        // Load: remove the old chunks of each file, then add the new ones
        for (Resource file : files) {
            String topic = file.getFilename().replace(".md", "");
            vectorStore.delete("topic == '" + topic + "'");
        }
        vectorStore.add(chunks);
        if (vectorStore instanceof SimpleVectorStore simple) {
            SimpleVectorStoreConfig.STORE_FILE.getParentFile().mkdirs();
            simple.save(SimpleVectorStoreConfig.STORE_FILE);
        }

        long millis = System.currentTimeMillis() - start;
        log.info("Ingested {} files, {} sections, {} chunks in {} ms",
            files.length, documents.size(), chunks.size(), millis);
        return new IngestResult(files.length, documents.size(), chunks.size(), millis);
    }

    public record IngestResult(int files, int sections, int chunks, long millis) {
    }
}
