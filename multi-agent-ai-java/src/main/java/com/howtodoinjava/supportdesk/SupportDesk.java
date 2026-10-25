package com.howtodoinjava.supportdesk;

import static dev.langchain4j.agentic.AgenticServices.agentBuilder;
import static dev.langchain4j.agentic.AgenticServices.conditionalBuilder;
import static dev.langchain4j.agentic.AgenticServices.loopBuilder;
import static dev.langchain4j.agentic.AgenticServices.sequenceBuilder;

import dev.langchain4j.agentic.UntypedAgent;
import dev.langchain4j.model.chat.ChatModel;

public class SupportDesk {

    static UntypedAgent build(ChatModel model) {
        TriageAgent triage = agentBuilder(TriageAgent.class).chatModel(model).build();
        BillingAgent billing = agentBuilder(BillingAgent.class).chatModel(model).build();
        ShippingAgent shipping = agentBuilder(ShippingAgent.class).chatModel(model).build();
        ReviewerAgent reviewer = agentBuilder(ReviewerAgent.class).chatModel(model).build();
        ReviserAgent reviser = agentBuilder(ReviserAgent.class).chatModel(model).build();

        // Routing: the triage result decides which specialist writes the draft
        UntypedAgent specialists = conditionalBuilder()
                .subAgents(scope -> scope.readState("department", Department.BILLING) == Department.BILLING, billing)
                .subAgents(scope -> scope.readState("department", Department.BILLING) == Department.SHIPPING, shipping)
                .build();

        // Loop with a critic: review, then revise, at most twice
        UntypedAgent reviewLoop = loopBuilder()
                .subAgents(reviewer, reviser)
                .exitCondition(scope -> scope.readState("review", "").strip().startsWith("APPROVED"))
                .maxIterations(2)
                .build();

        // Sequence: triage, then the specialist, then the review loop
        return sequenceBuilder()
                .subAgents(triage, specialists, reviewLoop)
                .outputKey("draft")
                .build();
    }
}
