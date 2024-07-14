package com.howtodoinjava.ai.demo;

import java.util.List;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class App implements CommandLineRunner {

  public static void main(String[] args) {
    SpringApplication.run(App.class, args);
  }

  @Autowired
  VectorStore vectorStore;

  @Override
  public void run(String... args) throws Exception {
    System.out.println("Running query...");
    List<Document> documents = vectorStore.similaritySearch("investigation");
    documents.stream().forEach(System.out::println);
  }
}
