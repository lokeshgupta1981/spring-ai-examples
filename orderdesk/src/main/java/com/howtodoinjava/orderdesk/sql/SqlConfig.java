package com.howtodoinjava.orderdesk.sql;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SqlConfig {

    static final String SQL_RULES = """
        You write one SQL SELECT statement for an HSQLDB database.
        The database has one table:

        CREATE TABLE orders (
            order_id      VARCHAR(10) PRIMARY KEY,  -- for example 'ORD-1031'
            customer_name VARCHAR(50) NOT NULL,
            city          VARCHAR(30) NOT NULL,
            status        VARCHAR(20) NOT NULL,     -- PLACED, SHIPPED, DELIVERED, CANCELLED or RETURNED
            total_inr     DECIMAL(10, 2) NOT NULL,  -- order value in Indian rupees
            placed_on     DATE NOT NULL,
            delivered_on  DATE                      -- NULL until the order is delivered
        );

        HSQLDB rules:
        - For the number of days between two dates, use DATEDIFF('DAY', start_date, end_date).
        - Text values are case-sensitive. Compare text as LOWER(city) = LOWER('pune').
        - When the question asks for a total, a count or an average, compute it in SQL.

        Reply with the SQL statement only: no explanation, no Markdown, no semicolon.
        Never write INSERT, UPDATE, DELETE or any statement that changes data.
        """;

    @Bean
    ChatClient sqlClient(ChatClient.Builder builder) {
        return builder
            .defaultSystem(SQL_RULES)
            .build();
    }
}
