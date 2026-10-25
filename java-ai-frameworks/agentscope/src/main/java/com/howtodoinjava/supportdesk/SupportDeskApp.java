package com.howtodoinjava.supportdesk;

import io.agentscope.core.ReActAgent;
import io.agentscope.core.message.Msg;
import io.agentscope.core.tool.Toolkit;
import io.agentscope.extensions.model.ollama.OllamaChatModel;

public class SupportDeskApp {

    public static void main(String[] args) {
        Toolkit toolkit = new Toolkit();
        toolkit.registerTool(new SupportTools());

        ReActAgent agent = ReActAgent.builder()
                .name("SupportDesk")
                .sysPrompt("You answer questions about support tickets.")
                .model(OllamaChatModel.builder()
                        .baseUrl("http://localhost:11434")
                        .modelName("qwen2.5:7b")
                        .build())
                .toolkit(toolkit)
                .build();

        Msg question = Msg.builder().textContent("What is the status of ticket T-1001?").build();
        Msg reply = agent.call(question).block();
        System.out.println(reply.getTextContent());
    }
}
