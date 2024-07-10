package com.howtodoinjava.ai.chromaWithOpenAI;

import java.util.List;
import java.util.Objects;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chroma.ChromaApi;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.vectorstore.ChromaVectorStore;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@SpringBootApplication
public class Application implements CommandLineRunner {

  // Start Chroma DB before running this example
  // docker run -it --rm --name chroma -p 8000:8000 ghcr.io/chroma-core/chroma:0.4.15

  public static void main(String[] args) {
    new SpringApplicationBuilder(Application.class)
      .run(args);
  }

  @Autowired
  EmbeddingModel embeddingModel;

  @Autowired
  ChatModel chatModel;

  @Autowired
  VectorStore vectorStore;

  @Value("classpath:/data/current-affairs.pdf")
  Resource pdfFile;

  @Value("classpath:/data/vectorStoreRawData.json")
  private String vectorStoreRawData;

  @Override
  public void run(String... args) throws Exception {

    Objects.requireNonNull(pdfFile);
    Objects.requireNonNull(vectorStore);
    Objects.requireNonNull(vectorStoreRawData);

    //1. Load Data

    PagePdfDocumentReader pdfReader = new PagePdfDocumentReader(this.pdfFile,
      PdfDocumentReaderConfig.builder()
        .withPageExtractedTextFormatter(ExtractedTextFormatter.builder()
          .withNumberOfBottomTextLinesToDelete(3)
          .withNumberOfTopPagesToSkipBeforeDelete(1)
          .build())
        .withPagesPerDocument(1)
        .build());

    List<Document> documents = pdfReader.read()
      .stream()
      .toList();

    vectorStore.add(documents);

    System.out.println("=============VECTORS ARE LOADED SUCCESSFULLY============");

    // Writing vectors to a file so that we do not need to generate from the embedded model
    /*var vectorStoreFile = new File("c:/temp/vectorStoreRawData.json");
    var simpleVectorStore = new SimpleVectorStore(embeddingModel);
    simpleVectorStore.add(documents);
    simpleVectorStore.save(vectorStoreFile);*/

    //2. Search Data

    ChatClient chatClient = ChatClient.builder(chatModel)
      .defaultAdvisors(new QuestionAnswerAdvisor(vectorStore, SearchRequest.defaults()))
      .build();

    String aiResponse = chatClient.prompt()
      .user("India’s first undersea tunnel is being constructed in which city?")
      .call()
      .content();

    System.out.println(aiResponse);
  }
}

@Configuration
class ApplicationConfiguration {

  @Bean
  public RestClient.Builder builder() {
    return RestClient.builder().requestFactory(new SimpleClientHttpRequestFactory());
  }

  @Bean
  public ChromaApi chromaApi(RestClient.Builder restClientBuilder) {
    String chromaUrl = "http://localhost:8000";
    ChromaApi chromaApi = new ChromaApi(chromaUrl, restClientBuilder);
    return chromaApi;
  }

  @Bean
  public VectorStore chromaVectorStore(EmbeddingModel embeddingModel, ChromaApi chromaApi) {
    return new ChromaVectorStore(embeddingModel, chromaApi, "TestCollection", false);
  }
}
