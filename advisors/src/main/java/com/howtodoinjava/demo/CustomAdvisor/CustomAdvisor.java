package com.howtodoinjava.demo.CustomAdvisor;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisor;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisorChain;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.util.Assert;
import reactor.core.publisher.Flux;

public class CustomAdvisor implements CallAdvisor, StreamAdvisor {

  private final static Logger logger = LoggerFactory.getLogger(CustomAdvisor.class);

  @Override
  public ChatClientResponse adviseCall(ChatClientRequest chatClientRequest, CallAdvisorChain callAdvisorChain) {
    Assert.notNull(chatClientRequest, "the chatClientRequest cannot be null");

    ChatClientRequest formattedChatClientRequest = augmentWithCustomInstructions(chatClientRequest);
    return callAdvisorChain.nextCall(chatClientRequest);
  }

  @Override
  public Flux<ChatClientResponse> adviseStream(ChatClientRequest chatClientRequest, StreamAdvisorChain streamAdvisorChain) {
    Assert.notNull(chatClientRequest, "the chatClientRequest cannot be null");

    ChatClientRequest formattedChatClientRequest = augmentWithCustomInstructions(chatClientRequest);
    return streamAdvisorChain.nextStream(chatClientRequest);
  }


  private static ChatClientRequest augmentWithCustomInstructions(ChatClientRequest chatClientRequest) {

    String customInstructions = "Please respond as you are explaining it to a 5-year-old child. " +
      "Use simple words and short sentences. " +
      "If you don't know the answer, say 'I don't know'.";

    Prompt augmentedPrompt = chatClientRequest.prompt()
      .augmentUserMessage(userMessage -> userMessage.mutate()
        .text(userMessage.getText() + System.lineSeparator() + customInstructions)
        .build());

    return ChatClientRequest.builder()
      .prompt(augmentedPrompt)
      .context(Map.copyOf(chatClientRequest.context()))
      .build();
  }


  @Override
  public String getName() {
    return "CustomAdvisor";
  }

  @Override
  public int getOrder() {
    return Integer.MAX_VALUE;
  }
}