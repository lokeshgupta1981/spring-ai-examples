package com.howtodoinjava.orderdesk.prompts;

import java.util.List;
import java.util.Map;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PromptController {

    private final ChatClient chatClient;
    private final Resource systemText;
    private final Resource replyTemplate;

    // A plain client: each endpoint sets its own system prompt
    PromptController(ChatClient.Builder builder,
                     @Value("classpath:prompts/support-system.st") Resource systemText,
                     @Value("classpath:prompts/complaint-reply.st") Resource replyTemplate) {
        this.chatClient = builder.build();
        this.systemText = systemText;
        this.replyTemplate = replyTemplate;
    }

    // Shows the text that the model receives, without calling the model
    @GetMapping("/prompts/render")
    String render(@RequestParam String customer, @RequestParam String orderId,
                  @RequestParam String complaint) {
        return new PromptTemplate(replyTemplate)
            .render(Map.of("customer", customer, "orderId", orderId, "complaint", complaint));
    }

    // System prompt and user template, both loaded from files
    @GetMapping("/prompts/reply")
    String reply(@RequestParam String customer, @RequestParam String orderId,
                 @RequestParam String complaint) {
        return chatClient.prompt()
            .system(systemText)
            .user(u -> u.text(replyTemplate)
                .param("customer", customer)
                .param("orderId", orderId)
                .param("complaint", complaint))
            .call()
            .content();
    }

    private static final String CLASSIFY = """
        Classify the customer message into exactly one category:
        RETURN, DELIVERY, PAYMENT, WARRANTY or OTHER.
        Reply with the category only.""";

    // Examples as earlier turns of the conversation: one user message, one answer
    private static final List<Message> EXAMPLES = List.of(
        new UserMessage("The parcel says delivered but I never got it."),
        new AssistantMessage("DELIVERY"),
        new UserMessage("Money left my account twice for one order."),
        new AssistantMessage("PAYMENT"),
        new UserMessage("My mixer stopped working after 3 months."),
        new AssistantMessage("WARRANTY"),
        new UserMessage("The shoes don't fit, I want to send them back."),
        new AssistantMessage("RETURN"),
        new UserMessage("Which cards do you accept?"),
        new AssistantMessage("PAYMENT"),
        new UserMessage("You sent me a blue bag but I ordered a black one."),
        new AssistantMessage("RETURN"));

    // Same task, with one line that says what each category covers
    private static final String CLASSIFY_DEFINED = """
        Classify the customer message into exactly one category:
        RETURN: the customer wants to send an item back or got the wrong item.
        DELIVERY: a parcel is late, lost or delivered to the wrong place.
        PAYMENT: anything about paying, charges, refunds of money or payment methods.
        WARRANTY: an item stopped working or is faulty after some use.
        OTHER: anything else, such as questions about products we sell.
        Reply with the category only.""";

    @GetMapping("/prompts/classify")
    String classify(@RequestParam String message,
                    @RequestParam(defaultValue = "false") boolean examples,
                    @RequestParam(defaultValue = "false") boolean defined) {
        var request = chatClient.prompt().system(defined ? CLASSIFY_DEFINED : CLASSIFY);
        if (examples) {
            request = request.messages(EXAMPLES);
        }
        return request.user(message).call().content();
    }
}
