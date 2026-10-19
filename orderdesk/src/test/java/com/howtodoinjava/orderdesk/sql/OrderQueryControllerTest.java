package com.howtodoinjava.orderdesk.sql;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

// A unit test without a real model: a fake ChatModel returns fixed answers
class OrderQueryControllerTest {

    private final Deque<String> modelReplies = new ArrayDeque<>();
    private OrderQueryController controller;

    @BeforeEach
    void setUp() {
        ChatModel fakeModel = prompt -> new ChatResponse(
            List.of(new Generation(new AssistantMessage(modelReplies.pop()))));
        ChatClient fakeClient = ChatClient.create(fakeModel);

        JdbcTemplate jdbc = new JdbcTemplate(new SimpleDriverDataSource(
            new org.hsqldb.jdbc.JDBCDriver(), "jdbc:hsqldb:mem:unit", "SA", ""));
        jdbc.execute("DROP TABLE IF EXISTS orders");
        jdbc.execute("CREATE TABLE orders (order_id VARCHAR(10), city VARCHAR(30))");
        jdbc.update("INSERT INTO orders VALUES ('ORD-1031', 'Pune'), ('ORD-1032', 'Mumbai')");

        controller = new OrderQueryController(fakeClient, fakeClient, new OrderQueryService(jdbc));
    }

    @Test
    void runsTheSqlAndExplainsTheRows() {
        modelReplies.add("```sql\nSELECT COUNT(*) FROM orders WHERE city = 'Pune';\n```");
        modelReplies.add("1 order went to Pune.");

        var answer = controller.ask("How many orders went to Pune?");

        assertThat(answer.sql()).isEqualTo("SELECT COUNT(*) FROM orders WHERE city = 'Pune'");
        assertThat(answer.rows()).hasSize(1);
        assertThat(answer.answer()).isEqualTo("1 order went to Pune.");
    }

    @Test
    void refusesSqlThatChangesData() {
        modelReplies.add("DELETE FROM orders");

        assertThatThrownBy(() -> controller.ask("Delete all orders"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageStartingWith("Only SELECT statements are allowed");
    }
}
