package com.howtodoinjava.ai.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;

@Component
public class EtlPipeline {

  private static final Logger LOGGER = LoggerFactory.getLogger(EtlPipeline.class);

  private final CustomDocumentReader documentReader;
  private final VectorStore vectorStore;
  private final TextSplitter textSplitter;

  public EtlPipeline(VectorStore vectorStore,
    TextSplitter textSplitter,
    CustomDocumentReader documentReader) {

    this.vectorStore = vectorStore;
    this.textSplitter = textSplitter;
    this.documentReader = documentReader;
  }

  public void runIngestion() {
    LOGGER.info("RunIngestion() started");
    vectorStore.write(textSplitter.apply(documentReader.get()));
    LOGGER.info("RunIngestion() finished");
  }
}
