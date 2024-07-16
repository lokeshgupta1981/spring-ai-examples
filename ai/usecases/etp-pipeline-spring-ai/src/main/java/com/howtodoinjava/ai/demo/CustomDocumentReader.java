package com.howtodoinjava.ai.demo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.document.DocumentReader;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Component;

@Component
public class CustomDocumentReader implements DocumentReader {

  private static final Logger LOGGER = LoggerFactory.getLogger(CustomDocumentReader.class);

  @Value("${input.directory}")
  private String inputDir;

  @Value("${input.filename.regex}")
  private String pattern;

  @SneakyThrows
  @Override
  public List<Document> get() {

    List<Document> documentList = new ArrayList<>();
    TikaDocumentReader tikaDocumentReader;

    Files.newDirectoryStream(Path.of(inputDir), pattern).forEach(path -> {
      List<Document> documents = null;
      try {
        documents = new TikaDocumentReader(new ByteArrayResource(Files.readAllBytes(path))).get()
          .stream().peek(document -> {
            document.getMetadata().put("source", path.getFileName());
            LOGGER.info("Reading new document :: {}", path.getFileName());
          }).toList();
      } catch (IOException e) {
        throw new RuntimeException("Error while reading the file : " + path.toUri() + "::" + e);
      }
      documentList.addAll(documents);
    });
    return documentList;
  }
}
