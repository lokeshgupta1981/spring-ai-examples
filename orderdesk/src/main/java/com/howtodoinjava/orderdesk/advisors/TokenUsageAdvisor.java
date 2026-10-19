package com.howtodoinjava.orderdesk.advisors;

import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisor;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisorChain;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.ai.chat.model.ChatResponse;

import reactor.core.publisher.Flux;

// Logs the time and the tokens of every ChatClient call that it advises
public class TokenUsageAdvisor implements CallAdvisor, StreamAdvisor {

    private static final Logger log = LoggerFactory.getLogger(TokenUsageAdvisor.class);

    private final int order;

    public TokenUsageAdvisor(int order) {
        this.order = order;
    }

    @Override
    public String getName() {
        return "TokenUsageAdvisor";
    }

    @Override
    public int getOrder() {
        return order;
    }

    @Override
    public ChatClientResponse adviseCall(ChatClientRequest request, CallAdvisorChain chain) {
        log.info("Chain: {}", chain.getCallAdvisors().stream()
            .map(a -> a.getName() + "(" + a.getOrder() + ")")
            .collect(Collectors.joining(" -> ")));
        long start = System.currentTimeMillis();

        ChatClientResponse response = chain.nextCall(request);   // the rest of the chain

        logUsage(response.chatResponse(), System.currentTimeMillis() - start);
        return response;
    }

    @Override
    public Flux<ChatClientResponse> adviseStream(ChatClientRequest request,
                                                 StreamAdvisorChain chain) {
        long start = System.currentTimeMillis();
        return chain.nextStream(request)
            .doOnNext(response -> {
                ChatResponse chatResponse = response.chatResponse();
                // only the last piece of a stream carries the token counts
                if (chatResponse != null && chatResponse.getMetadata().getUsage() != null
                        && chatResponse.getMetadata().getUsage().getTotalTokens() != null
                        && chatResponse.getMetadata().getUsage().getTotalTokens() > 0) {
                    logUsage(chatResponse, System.currentTimeMillis() - start);
                }
            });
    }

    private void logUsage(ChatResponse chatResponse, long millis) {
        Usage usage = chatResponse.getMetadata().getUsage();
        log.info("Tokens: prompt={}, completion={}, total={}, time={} ms",
            usage.getPromptTokens(), usage.getCompletionTokens(), usage.getTotalTokens(), millis);
    }
}
