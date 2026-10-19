package com.howtodoinjava.orderdesk.rag;

import java.util.List;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.document.Document;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RagController {

    private final ChatClient supportClient;
    private final QuestionAnswerAdvisor questionAnswerAdvisor;
    private final RetrievalAugmentationAdvisor retrievalAugmentationAdvisor;

    RagController(@Qualifier("supportClient") ChatClient supportClient,
                  QuestionAnswerAdvisor questionAnswerAdvisor,
                  RetrievalAugmentationAdvisor retrievalAugmentationAdvisor) {
        this.supportClient = supportClient;
        this.questionAnswerAdvisor = questionAnswerAdvisor;
        this.retrievalAugmentationAdvisor = retrievalAugmentationAdvisor;
    }

    @GetMapping("/rag/qa")
    RagAnswer askWithQuestionAnswer(@RequestParam String question) {
        ChatClientResponse response = supportClient.prompt()
            .user(question)
            .advisors(questionAnswerAdvisor)
            .call()
            .chatClientResponse();
        List<Document> documents = (List<Document>) response.context()
            .get(QuestionAnswerAdvisor.RETRIEVED_DOCUMENTS);
        return RagAnswer.of(response, documents);
    }

    @GetMapping("/rag/ra")
    RagAnswer askWithRetrievalAugmentation(@RequestParam String question) {
        ChatClientResponse response = supportClient.prompt()
            .user(question)
            .advisors(retrievalAugmentationAdvisor)
            .call()
            .chatClientResponse();
        List<Document> documents = (List<Document>) response.context()
            .get(RetrievalAugmentationAdvisor.DOCUMENT_CONTEXT);
        return RagAnswer.of(response, documents);
    }

    record RagAnswer(String answer, List<String> sources) {

        static RagAnswer of(ChatClientResponse response, List<Document> documents) {
            String answer = response.chatResponse().getResult().getOutput().getText();
            List<String> sources = documents == null ? List.of() : documents.stream()
                .map(d -> d.getMetadata().get("source") + ": " + d.getText())
                .toList();
            return new RagAnswer(answer, sources);
        }
    }
}
