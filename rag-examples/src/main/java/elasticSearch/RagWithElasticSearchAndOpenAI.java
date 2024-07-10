package elasticSearch;

import java.util.Objects;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@SpringBootApplication
public class RagWithElasticSearchAndOpenAI implements CommandLineRunner {

  public static void main(String[] args) {
    new SpringApplicationBuilder(RagWithElasticSearchAndOpenAI.class)
       .web(WebApplicationType.NONE)
       .run(args);
  }

  @Value("classpath:/data/statement.pdf")
  Resource pdfFile;

  /*@Autowired
  private ElasticsearchVectorStore vectorStore;*/

  @Autowired
  OpenAiEmbeddingModel embeddingModel;
  @Override
  public void run(String... args) throws Exception {

    Objects.requireNonNull(pdfFile);
    Objects.requireNonNull(embeddingModel);
    //Objects.requireNonNull(vectorStore);

    /*//1. Read Pdf Document

    PagePdfDocumentReader pdfReader = new PagePdfDocumentReader(this.pdfFile,
      PdfDocumentReaderConfig.builder()
        .withPageExtractedTextFormatter(ExtractedTextFormatter.builder()
          .withNumberOfBottomTextLinesToDelete(3)
          .withNumberOfTopPagesToSkipBeforeDelete(1)
          .build())
        .withPagesPerDocument(1)
        .build());

    List<String> documents = pdfReader.read()
      .stream()
      .map(document -> document.getContent())
      .toList();

    // 2. Create embeddings

    EmbeddingResponse embeddingResponse = embeddingModel.call(
      new EmbeddingRequest(documents,
        OpenAiEmbeddingOptions.builder()
          .withModel("text-embedding-3-small")
          .build()));

    embeddingResponse.getResults().stream().forEach(System.out::println);*/

    //3. Store embedding in elastic-search database
    //vectorStore.



  }
}

@Configuration
class ApplicationConfiguration {

}
