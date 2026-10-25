package com.howtodoinjava.supportdesk;

import static dev.langchain4j.agentic.AgenticServices.agentBuilder;
import static dev.langchain4j.agentic.AgenticServices.supervisorBuilder;

import java.time.Duration;
import java.util.List;

import dev.langchain4j.agentic.supervisor.SupervisorAgent;
import dev.langchain4j.agentic.supervisor.SupervisorResponseStrategy;
import dev.langchain4j.model.ollama.OllamaChatModel;

public class SupervisorDemo {

    public static void main(String[] args) {
        CallCounter counter = new CallCounter();
        OllamaChatModel model = OllamaChatModel.builder()
                .baseUrl("http://localhost:11434")
                .modelName(System.getProperty("model", "qwen2.5:7b"))
                .temperature(0.0)
                .timeout(Duration.ofMinutes(5))
                .listeners(List.of(counter))
                .build();

        BillingAgent billing = agentBuilder(BillingAgent.class).chatModel(model).build();
        ShippingAgent shipping = agentBuilder(ShippingAgent.class).chatModel(model).build();

        // The supervisor model decides which agent to call, and when to stop
        SupervisorAgent supervisor = supervisorBuilder()
                .chatModel(model)
                .subAgents(billing, shipping)
                .responseStrategy(SupervisorResponseStrategy.LAST)
                .maxAgentsInvocations(3)
                .build();

        for (Ticket ticket : Ticket.TEST_SET.subList(0, 3)) {
            counter.reset();
            String reply = supervisor.invoke("Answer support ticket " + ticket.id() + ": " + ticket.text());
            System.out.printf("SUPERVISOR|%s|calls=%d|tokens=%d|%s%n", ticket.id(),
                    counter.calls.get(), counter.tokens.get(), reply.replace("\n", "\\n"));
        }
    }
}
