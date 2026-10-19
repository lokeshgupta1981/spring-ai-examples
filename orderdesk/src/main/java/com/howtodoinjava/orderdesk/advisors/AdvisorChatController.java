package com.howtodoinjava.orderdesk.advisors;

import java.util.List;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SafeGuardAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.Ordered;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Flux;

@RestController
public class AdvisorChatController {

    // The default QuestionAnswerAdvisor template says "not prior knowledge",
    // so the model ignored the chat history; this one allows both
    static final PromptTemplate POLICY_AND_HISTORY = new PromptTemplate("""
        {query}

        Answer from the conversation so far and from the policy text below.
        ---------------------
        {question_answer_context}
        ---------------------
        If neither of them has the answer, say that you don't know.
        """);

    private final ChatClient supportClient;
    private final List<Advisor> advisors;
    private final TokenUsageAdvisor tokenUsageAdvisor =
        new TokenUsageAdvisor(Ordered.HIGHEST_PRECEDENCE + 100);

    AdvisorChatController(@Qualifier("supportClient") ChatClient supportClient,
                          ChatMemory chatMemory, VectorStore vectorStore) {
        this.supportClient = supportClient;
        this.advisors = List.of(
            // 1. block sensitive input before anything else sees or stores it
            new SafeGuardAdvisor(List.of("password", "card number", "CVV"),
                "Please don't share payment details or passwords in chat.",
                Ordered.HIGHEST_PRECEDENCE),
            // 2. measure the whole call, including memory and RAG
            tokenUsageAdvisor,
            // 3. add the conversation history (default order HIGHEST_PRECEDENCE + 200)
            MessageChatMemoryAdvisor.builder(chatMemory).build(),
            // 4. add the policy sections that match the question
            QuestionAnswerAdvisor.builder(vectorStore)
                .searchRequest(SearchRequest.builder().topK(3).similarityThreshold(0.5).build())
                .promptTemplate(POLICY_AND_HISTORY)
                .order(0)
                .build(),
            // 5. log the final prompt, just before the model call
            new SimpleLoggerAdvisor(Ordered.LOWEST_PRECEDENCE - 1));
    }

    @GetMapping("/advisors/ask")
    String ask(@RequestParam String customerId, @RequestParam String question) {
        return supportClient.prompt()
            .user(question)
            .advisors(advisors)
            .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, customerId))
            .call()
            .content();
    }

    @GetMapping("/advisors/stream")
    Flux<String> stream(@RequestParam String question) {
        return supportClient.prompt()
            .user(question)
            .advisors(tokenUsageAdvisor)
            .stream()
            .content();
    }
}
