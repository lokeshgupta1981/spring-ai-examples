package com.howtodoinjava.supportdesk;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.service.AiServices;

public class SupportDeskApp {

    public static void main(String[] args) {
        ChatModel model = OllamaChatModel.builder()
                .baseUrl("http://localhost:11434")
                .modelName("qwen2.5:7b")
                .build();

        SupportAgent agent = AiServices.builder(SupportAgent.class)
                .chatModel(model)
                .tools(new SupportTools())
                .build();

        System.out.println(agent.chat("What is the status of ticket T-1001?"));
    }
}
