package com.howtodoinjava.supportdesk;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import dev.langchain4j.agentic.UntypedAgent;
import dev.langchain4j.agentic.scope.ResultWithAgenticScope;
import dev.langchain4j.model.chat.request.ResponseFormat;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.service.AiServices;

public class SupportDeskApp {

    public static void main(String[] args) {
        String mode = args.length > 0 ? args[0] : "multi";
        String modelName = System.getProperty("model", "qwen2.5:7b");
        CallCounter counter = new CallCounter();

        OllamaChatModel model = OllamaChatModel.builder()
                .baseUrl("http://localhost:11434")
                .modelName(modelName)
                .temperature(0.0)
                .timeout(Duration.ofMinutes(5))
                .listeners(List.of(counter))
                .build();

        UntypedAgent desk = SupportDesk.build(model);
        // The single agent returns a record, so it gets Ollama's JSON mode
        OllamaChatModel jsonModel = OllamaChatModel.builder()
                .baseUrl("http://localhost:11434")
                .modelName(modelName)
                .temperature(0.0)
                .timeout(Duration.ofMinutes(5))
                .responseFormat(ResponseFormat.JSON)
                .listeners(List.of(counter))
                .build();
        SingleSupportAgent single = AiServices.create(SingleSupportAgent.class, jsonModel);

        int limit = Integer.getInteger("limit", Ticket.TEST_SET.size());
        for (Ticket ticket : Ticket.TEST_SET.subList(0, limit)) {
            counter.reset();
            long start = System.nanoTime();
            Department department = null;
            String reply;
            try {
            if (mode.equals("single")) {
                SupportAnswer answer = single.answer(ticket.id(), ticket.text());
                department = answer.department();
                reply = answer.reply();
            } else {
                ResultWithAgenticScope<String> result = desk.invokeWithAgenticScope(
                        Map.of("ticketId", ticket.id(), "ticket", ticket.text()));
                department = (Department) result.agenticScope().readState("department");
                reply = result.result();
            }
            } catch (RuntimeException e) {
                reply = "ERROR " + e.getClass().getSimpleName() + " " + e.getMessage();
            }
            long millis = (System.nanoTime() - start) / 1_000_000;
            System.out.printf("RESULT|%s|%s|%s|%s|%d|%d|%d|%s%n", mode, ticket.id(), ticket.expected(),
                    department, counter.calls.get(), counter.tokens.get(), millis,
                    reply == null ? "" : reply.replace("\n", "\\n"));
        }
    }
}
