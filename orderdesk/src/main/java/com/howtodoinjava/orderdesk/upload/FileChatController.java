package com.howtodoinjava.orderdesk.upload;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.howtodoinjava.orderdesk.vectorstore.SimpleVectorStoreConfig;

@RestController
public class FileChatController {

    static final String FILE_RULES = """
        You answer questions about one document that a customer uploaded.
        Answer from the document text given with the question.
        If the document has nothing related to the question, say that it does not cover it.
        Answer in at most three sentences.
        """;

    private final VectorStore vectorStore;
    private final ChatClient fileClient;
    private final QuestionAnswerAdvisor fileAdvisor;
    private final Map<String, UploadedFile> files = new ConcurrentHashMap<>();

    FileChatController(VectorStore vectorStore, ChatClient.Builder builder) {
        this.vectorStore = vectorStore;
        this.fileClient = builder.defaultSystem(FILE_RULES).build();
        this.fileAdvisor = QuestionAnswerAdvisor.builder(vectorStore)
            .searchRequest(SearchRequest.builder().topK(4).build())
            .build();
    }

    @PostMapping("/files")
    UploadedFile upload(@RequestParam("file") MultipartFile file) {
        String fileId = UUID.randomUUID().toString().replace("-", "").substring(0, 12);
        Resource resource = file.getResource();

        // Tika reads PDF, Word, text, HTML and many other formats
        List<Document> documents = new TikaDocumentReader(resource).get();
        List<Document> chunks = TokenTextSplitter.builder()
            .withChunkSize(300)
            .build()
            .apply(documents);
        chunks.forEach(chunk -> {
            chunk.getMetadata().put("file_id", fileId);
            chunk.getMetadata().put("file_name", file.getOriginalFilename());
        });
        vectorStore.add(chunks);
        saveIfSimpleStore();

        UploadedFile uploaded = new UploadedFile(fileId, file.getOriginalFilename(), chunks.size());
        files.put(fileId, uploaded);
        return uploaded;
    }

    @GetMapping("/files")
    Collection<UploadedFile> list() {
        return files.values();
    }

    @GetMapping("/files/{fileId}/ask")
    String ask(@PathVariable String fileId, @RequestParam String question) {
        return fileClient.prompt()
            .user(question)
            .advisors(fileAdvisor)
            // search only the chunks of this one file
            .advisors(a -> a.param(QuestionAnswerAdvisor.FILTER_EXPRESSION,
                "file_id == '" + fileId + "'"))
            .call()
            .content();
    }

    @DeleteMapping("/files/{fileId}")
    ResponseEntity<Void> delete(@PathVariable String fileId) {
        vectorStore.delete("file_id == '" + fileId + "'");
        saveIfSimpleStore();
        files.remove(fileId);
        return ResponseEntity.noContent().build();
    }

    private void saveIfSimpleStore() {
        if (vectorStore instanceof SimpleVectorStore simple) {
            simple.save(SimpleVectorStoreConfig.STORE_FILE);
        }
    }

    record UploadedFile(String fileId, String fileName, int chunks) {
    }
}
