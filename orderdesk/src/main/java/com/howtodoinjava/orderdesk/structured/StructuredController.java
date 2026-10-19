package com.howtodoinjava.orderdesk.structured;

import java.util.List;
import java.util.Map;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.StructuredOutputValidationAdvisor;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StructuredController {

    private static final String TRIAGE = """
        You sort customer messages for the OrderDesk support team.
        Category: RETURN, DELIVERY, PAYMENT, WARRANTY or OTHER.
        Order id: copy it exactly if the message has one, such as ORD-1042, else write NONE.
        Priority HIGH: money lost, damaged or missing items. LOW: questions only.""";

    private final ChatClient chatClient;

    StructuredController(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    // The text that entity() adds to the prompt for TicketTriage
    @GetMapping("/structured/format")
    String format() {
        return new BeanOutputConverter<>(TicketTriage.class).getFormat();
    }

    // 1. One record
    @GetMapping("/structured/triage")
    TicketTriage triage(@RequestParam String message) {
        return chatClient.prompt()
            .system(TRIAGE)
            .user(message)
            .call()
            .entity(TicketTriage.class);
    }

    // 2. A list of records
    @GetMapping("/structured/lines")
    List<OrderLine> lines(@RequestParam String message) {
        return chatClient.prompt()
            .user("List every product and its quantity in this message: " + message)
            .call()
            .entity(new ParameterizedTypeReference<List<OrderLine>>() {
            });
    }

    // 3. A map, when the keys are not known in advance
    @GetMapping("/structured/details")
    Map<String, Object> details(@RequestParam String message) {
        return chatClient.prompt()
            .user("Extract every detail of this delivery address as key-value pairs: " + message)
            .call()
            .entity(new ParameterizedTypeReference<Map<String, Object>>() {
            });
    }

    // 4. The provider enforces the JSON schema (Ollama's "format" parameter)
    @GetMapping("/structured/triage-native")
    TicketTriage triageNative(@RequestParam String message) {
        return chatClient.prompt()
            .system(TRIAGE)
            .user(message)
            .call()
            .entity(TicketTriage.class, spec -> spec.useProviderStructuredOutput());
    }

    // 5. Native output plus a schema check that asks again when the JSON doesn't match
    @GetMapping("/structured/triage-validated")
    TicketTriage triageValidated(@RequestParam String message) {
        return chatClient.prompt()
            .system(TRIAGE)
            .user(message)
            .advisors(StructuredOutputValidationAdvisor.builder()
                .outputType(TicketTriage.class)
                .maxRepeatAttempts(2)
                .build())
            .call()
            .entity(TicketTriage.class, spec -> spec.useProviderStructuredOutput());
    }
}
