package com.howtodoinjava.ai.demo;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.messaging.Message;
import reactor.core.publisher.Flux;

@Configuration
public class CloudFunctionConfig {

  private static final Logger LOGGER = LoggerFactory.getLogger(CloudFunctionConfig.class);


  @Bean
  Function<Flux<Message<byte[]>>, Flux<List<Document>>> documentReader() {
    return resourceFlux -> resourceFlux
      .map(message ->
        new TikaDocumentReader(new ByteArrayResource(message.getPayload()))
          .get()
          .stream()
          .peek(document -> {
            document.getMetadata()
              .put("source", message.getHeaders().get("file_name"));
          })
          .toList()
      );
  }

  @Bean
  Function<Flux<List<Document>>, Flux<List<Document>>> documentTransformer() {
    return documentListFlux ->
      documentListFlux
        .map(unsplitList -> new TokenTextSplitter().apply(unsplitList));
  }

  @Bean
  Consumer<Flux<List<Document>>> documentWriter(VectorStore vectorStore) {
    return documentFlux -> documentFlux
      .doOnNext(documents -> {
        LOGGER.info("Writing {} documents to vector store.", documents.size());
        vectorStore.accept(documents);
        LOGGER.info("{} documents have been written to vector store.", documents.size());
      })
      .subscribe();
  }
}
