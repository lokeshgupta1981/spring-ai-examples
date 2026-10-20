package com.howtodoinjava.orderdesk.moderation;

import java.util.List;

import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

// Checks the customer's message before any other advisor or the chat model sees it
@Component
public class ModerationAdvisor implements CallAdvisor {

    private static final String BLOCKED =
        "I can't help with this message. Please contact our support team by phone.";

    private final SafetyCheck safetyCheck;

    ModerationAdvisor(SafetyCheck safetyCheck) {
        this.safetyCheck = safetyCheck;
    }

    @Override
    public ChatClientResponse adviseCall(ChatClientRequest request, CallAdvisorChain chain) {
        String text = request.prompt().getUserMessage().getText();
        Verdict verdict = safetyCheck.check(text);
        if (verdict.safe()) {
            return chain.nextCall(request);
        }
        // Answer without calling the chat model
        ChatResponse blocked = ChatResponse.builder()
            .generations(List.of(new Generation(new AssistantMessage(BLOCKED))))
            .build();
        return ChatClientResponse.builder()
            .chatResponse(blocked)
            .context(request.context())
            .build();
    }

    @Override
    public String getName() {
        return "ModerationAdvisor";
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
