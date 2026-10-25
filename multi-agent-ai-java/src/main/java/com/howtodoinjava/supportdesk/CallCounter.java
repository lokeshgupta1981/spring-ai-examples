package com.howtodoinjava.supportdesk;

import java.util.concurrent.atomic.AtomicInteger;

import dev.langchain4j.model.chat.listener.ChatModelListener;
import dev.langchain4j.model.chat.listener.ChatModelResponseContext;
import dev.langchain4j.model.output.TokenUsage;

public class CallCounter implements ChatModelListener {

    final AtomicInteger calls = new AtomicInteger();
    final AtomicInteger tokens = new AtomicInteger();

    @Override
    public void onResponse(ChatModelResponseContext context) {
        calls.incrementAndGet();
        TokenUsage usage = context.chatResponse().tokenUsage();
        if (usage != null && usage.totalTokenCount() != null) {
            tokens.addAndGet(usage.totalTokenCount());
        }
    }

    void reset() {
        calls.set(0);
        tokens.set(0);
    }
}
