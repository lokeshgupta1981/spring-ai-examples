package com.howtodoinjava.ai.chat.redis.service;

/*import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;*/

//@Service
public class DataIngestionService {

  /*private static final Logger logger = LoggerFactory.getLogger(DataIngestionService.class);

  @Value("classpath:/data/spring-boot-reference.pdf")
  Resource pdfFile;

  @Autowired
  VectorStore vectorStore;

  public void load() {
    PagePdfDocumentReader pdfReader = new PagePdfDocumentReader(this.pdfFile,
      PdfDocumentReaderConfig.builder()
        .withPageExtractedTextFormatter(ExtractedTextFormatter.builder()
          .withNumberOfBottomTextLinesToDelete(3)
          .withNumberOfTopPagesToSkipBeforeDelete(1)
          .build())
        .withPagesPerDocument(1)
        .build());

    var tokenTextSplitter = new TokenTextSplitter();
    this.vectorStore.accept(tokenTextSplitter.apply(pdfReader.get()));
  }*/
}
