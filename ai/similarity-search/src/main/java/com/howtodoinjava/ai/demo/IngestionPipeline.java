package com.howtodoinjava.ai.demo;

import jakarta.annotation.PostConstruct;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;

@Component
public class IngestionPipeline {

  private static final Logger logger = LoggerFactory.getLogger(IngestionPipeline.class);
  private final VectorStore vectorStore;

  public IngestionPipeline(VectorStore vectorStore) {
    this.vectorStore = vectorStore;
  }

  @PostConstruct
  public void run() {
    var instrumentNotes = List.of(
      new LogRecord("INFO - Processing request for user authentication."),
      new LogRecord("DEBUG - Validating user credentials against the database."),
      new LogRecord("WARN - User authentication failed due to invalid credentials."),
      new LogRecord("ERROR - Failed to connect to external service. Retrying..."),
      new LogRecord("INFO - Successfully processed payment transaction."),
      new LogRecord("DEBUG - Fetching data from backend API."),
      new LogRecord("WARN - Unauthorized access attempt detected."),
      new LogRecord("ERROR - Database connection timed out."),
      new LogRecord("INFO - Starting server initialization."),
      new LogRecord("DEBUG - Loading configuration settings."),
      new LogRecord("WARN - Resource not found. Returning 404."),
      new LogRecord("ERROR - Unexpected server error occurred."),
      new LogRecord("INFO - Sending email notification."),
      new LogRecord("DEBUG - Parsing JSON response from API."),
      new LogRecord("WARN - Deprecated API endpoint accessed."),
      new LogRecord("ERROR - Out of memory error. Consider optimizing resources."),
      new LogRecord("INFO - User registration successful."),
      new LogRecord("DEBUG - Executing batch job."),
      new LogRecord("WARN - Insufficient permissions for this operation."),
      new LogRecord("ERROR - Failed to process request due to unknown error.")
      );

    logger.info("Creating InstrumentNotes as Documents");
    List<Document> documents = instrumentNotes.stream()
      .map(note -> new Document(note.content()))
      .toList();

    logger.info("Creating and storing Embeddings from Documents");
    vectorStore.add(documents);
  }
}
