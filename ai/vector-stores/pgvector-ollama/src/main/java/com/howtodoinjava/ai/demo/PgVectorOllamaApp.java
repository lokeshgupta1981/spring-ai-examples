package com.howtodoinjava.ai.demo;

import java.util.List;
import java.util.Map;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PgVectorOllamaApp implements CommandLineRunner {

  public static void main(String[] args) {
    SpringApplication.run(PgVectorOllamaApp.class);
  }

  private final VectorStore vectorStore;

  public PgVectorOllamaApp(VectorStore vectorStore) {
    this.vectorStore = vectorStore;
  }

  @Override
  public void run(String... args) {
    List<Document> documents = List.of(
      new Document("Java is a high-level, object-oriented programming language known for its platform independence."),
      new Document("It is widely used for developing enterprise applications, Android apps, and big data processing systems."),
      new Document("Java's strong typing, automatic memory management, and extensive libraries contribute to its popularity.", Map.of("reason", "popularity")));

    // Add the documents to PGVector
    vectorStore.add(documents);

    // Retrieve documents similar to a query
    List<Document> results = vectorStore.similaritySearch(SearchRequest.query("programming language").withTopK(1));

    results.stream()
      .map(Document::getContent)
      .forEach(System.out::println);
  }
}
