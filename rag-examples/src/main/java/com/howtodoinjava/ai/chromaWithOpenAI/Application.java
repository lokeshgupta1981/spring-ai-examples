package com.howtodoinjava.ai.chromaWithOpenAI;

import java.util.List;
import java.util.Objects;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chroma.vectorstore.ChromaApi;
import org.springframework.ai.chroma.vectorstore.ChromaVectorStore;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
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

  public Application(EmbeddingModel embeddingModel, OpenAiChatModel openAiChatModel,
    VectorStore vectorStore) {
    this.embeddingModel = embeddingModel;
    this.chatModel = openAiChatModel;
    this.vectorStore = vectorStore;
  }

  // Start Chroma DB before running this example
  // docker run -it --rm --name chroma -p 8000:8000 ghcr.io/chroma-core/chroma:0.4.15

  public static void main(String[] args) {
    new SpringApplicationBuilder(Application.class)
      .run(args);
  }

  final EmbeddingModel embeddingModel;
  final OpenAiChatModel chatModel;
  final VectorStore vectorStore;

  @Value("classpath:/data/current-affairs.pdf")
  Resource pdfFile;

  @Value("classpath:/data/vectorStoreRawData.json")
  private String vectorStoreRawData;

  @Override
  public void run(String... args) {

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

    QuestionAnswerAdvisor qaAdvisor = QuestionAnswerAdvisor.builder(vectorStore)
      .searchRequest(SearchRequest.builder().similarityThreshold(0.8d).topK(6).build())
      .build();

    ChatClient chatClient = ChatClient.builder(chatModel)
      .defaultAdvisors(qaAdvisor).build();

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
    return ChromaApi.builder().restClientBuilder(restClientBuilder).baseUrl(chromaUrl).build();
  }

  @Bean
  public VectorStore chromaVectorStore(EmbeddingModel embeddingModel, ChromaApi chromaApi) {
    return ChromaVectorStore.builder(chromaApi, embeddingModel)
      .collectionName("TestCollection")
      .initializeSchema(false)
      .build();
  }

  @Bean
  public OpenAiApi api() {
    OpenAiApi openAiApi = OpenAiApi.builder()
      .apiKey(System.getenv("OPENAI_API_KEY"))
      .build();
    return openAiApi;
  }

  @Bean
  public OpenAiChatModel openAiChatModel(OpenAiApi openAiApi) {
    return OpenAiChatModel.builder()
      .openAiApi(openAiApi)
      .build();
  }

  @Bean
  public EmbeddingModel embeddingModel(OpenAiApi openAiApi) {
    return new OpenAiEmbeddingModel(openAiApi);
  }
}
