package com.howtodoinjava.ai.demo;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.Resource;

//@Component
public class VectorStoreLoader implements ApplicationRunner {

  @Value("classpath:/CallingRates.pdf")
  Resource pdfResource;

  @Value("classpath:/story.txt")
  Resource txtResource;

  @Value("classpath:/story.md")
  Resource mdResource;

  @Autowired
  VectorStore vectorStore;

  @Override
  public void run(ApplicationArguments args) throws Exception {

    List<Document> documents = new ArrayList<>();

    TikaDocumentReader reader = new TikaDocumentReader(pdfResource);
    documents.addAll(reader.get());
    vectorStore.add(documents);

    var textReader1 = new TextReader(txtResource);
    textReader1.setCharset(Charset.defaultCharset());
    documents.addAll(textReader1.get());

    var textReader2 = new TextReader(mdResource);
    textReader2.setCharset(Charset.defaultCharset());
    documents.addAll(textReader2.get());

    vectorStore.add(new TokenTextSplitter(300, 300, 5, 1000, true).split(documents));

    System.out.println("Added documents to vector store");
  }
}
