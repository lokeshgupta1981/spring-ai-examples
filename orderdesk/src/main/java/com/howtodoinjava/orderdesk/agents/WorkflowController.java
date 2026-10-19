package com.howtodoinjava.orderdesk.agents;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.howtodoinjava.orderdesk.tools.OrderTools;

@RestController
public class WorkflowController {

    private static final Logger log = LoggerFactory.getLogger(WorkflowController.class);

    private final ChatClient supportClient;
    private final ChatClient workerClient;
    private final QuestionAnswerAdvisor policyAdvisor;
    private final OrderTools orderTools;

    WorkflowController(@Qualifier("supportClient") ChatClient supportClient,
                       @Qualifier("workerClient") ChatClient workerClient,
                       QuestionAnswerAdvisor policyAdvisor, OrderTools orderTools) {
        this.supportClient = supportClient;
        this.workerClient = workerClient;
        this.policyAdvisor = policyAdvisor;
        this.orderTools = orderTools;
    }

    // 1. Chain: each step's output is the next step's input
    @GetMapping("/agents/chain")
    List<String> chain(@RequestParam String complaint) {
        String summary = workerClient.prompt()
            .user("Summarize this customer complaint in one sentence: " + complaint)
            .call().content();
        String draft = supportClient.prompt()
            .user("Write a reply to this complaint: " + summary)
            .advisors(policyAdvisor)
            .call().content();
        String polite = workerClient.prompt()
            .user("Rewrite this reply so it is polite and at most three sentences. "
                + "Keep every fact. Reply: " + draft)
            .call().content();
        return List.of("SUMMARY: " + summary, "DRAFT: " + draft, "FINAL: " + polite);
    }

    // 2. Routing: classify first, then send the question to the right handler
    enum Route { POLICY, ORDER, OTHER }

    record RouteDecision(Route route, String reason) {
    }

    @GetMapping("/agents/route")
    List<String> route(@RequestParam String question) {
        RouteDecision decision = workerClient.prompt()
            .user("""
                Classify the customer question.
                POLICY: about returns, delivery, warranty or payment rules.
                ORDER: about one specific order or a customer's orders.
                OTHER: anything else.
                Question: """ + question)
            .call()
            .entity(RouteDecision.class);
        log.info("Route: {}", decision);

        String answer = switch (decision.route()) {
            case POLICY -> supportClient.prompt().user(question)
                .advisors(policyAdvisor).call().content();
            case ORDER -> supportClient.prompt().user(question)
                .tools(orderTools).call().content();
            case OTHER -> "OrderDesk can help with orders and store policies only.";
        };
        return List.of("ROUTE: " + decision.route() + " (" + decision.reason() + ")",
            "ANSWER: " + answer);
    }

    // 3. Parallelization: independent checks run at the same time
    record PolicyCheck(String policy, boolean ok, String issue) {
    }

    @GetMapping("/agents/parallel")
    List<PolicyCheck> parallel(@RequestParam String reply) throws Exception {
        List<String> rules = List.of(
            "returns: items can be returned within 10 days of delivery",
            "shipping: delivery is free above INR 499, otherwise INR 49",
            "payments: cash on delivery is allowed up to INR 5,000");
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<CompletableFuture<PolicyCheck>> checks = rules.stream()
                .map(rule -> CompletableFuture.supplyAsync(() -> workerClient.prompt()
                    .user("""
                        Policy: %s
                        Support reply: %s
                        Does the reply contradict this policy? Set ok to false only if it
                        states something different from the policy.""".formatted(rule, reply))
                    .call()
                    .entity(PolicyCheck.class), executor))
                .toList();
            return checks.stream().map(CompletableFuture::join).toList();
        }
    }

    // 4. Orchestrator and workers: one call plans the subtasks, workers do them
    record Plan(List<String> subtasks) {
    }

    @GetMapping("/agents/orchestrate")
    List<String> orchestrate(@RequestParam String request) {
        Plan plan = workerClient.prompt()
            .user("""
                Split this request into at most four separate questions that a support
                agent can answer one by one from the store policies. Request: """ + request)
            .call()
            .entity(Plan.class);
        log.info("Plan: {}", plan.subtasks());

        List<String> results = new ArrayList<>();
        for (String subtask : plan.subtasks()) {
            // each worker gets the whole request too, so details such as the city survive
            String answer = supportClient.prompt()
                .user("Customer request: " + request + "\nAnswer only this part: " + subtask)
                .advisors(policyAdvisor).call().content();
            results.add(subtask + " -> " + answer);
        }
        return results;
    }

    // 5. Evaluator and optimizer: draft, judge, improve, at most three rounds
    record Evaluation(boolean pass, String feedback) {
    }

    @GetMapping("/agents/refine")
    List<String> refine(@RequestParam String complaint) {
        List<String> rounds = new ArrayList<>();
        String reply = supportClient.prompt()
            .user("Write a reply to this customer: " + complaint)
            .advisors(policyAdvisor)
            .call().content();
        for (int round = 1; round <= 3; round++) {
            Evaluation evaluation = workerClient.prompt()
                .user("""
                    Judge this support reply. It passes only if it is at most three sentences,
                    is polite, and gives the customer one clear next step.
                    Customer: %s
                    Reply: %s""".formatted(complaint, reply))
                .call()
                .entity(Evaluation.class);
            rounds.add("ROUND " + round + ": " + reply + " | PASS=" + evaluation.pass()
                + " | " + evaluation.feedback());
            if (evaluation.pass() || round == 3) {
                break;   // passed, or no rounds left to judge an improved reply
            }
            reply = supportClient.prompt()
                .user("""
                    Improve this reply using the feedback. Keep every fact.
                    Reply: %s
                    Feedback: %s""".formatted(reply, evaluation.feedback()))
                .call().content();
        }
        return rounds;
    }
}
