package com.howtodoinjava.orderdesk.sql;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderQueryController {

    private static final Logger log = LoggerFactory.getLogger(OrderQueryController.class);

    private final ChatClient sqlClient;
    private final ChatClient supportClient;
    private final OrderQueryService orderQueryService;

    OrderQueryController(@Qualifier("sqlClient") ChatClient sqlClient,
                         @Qualifier("supportClient") ChatClient supportClient,
                         OrderQueryService orderQueryService) {
        this.sqlClient = sqlClient;
        this.supportClient = supportClient;
        this.orderQueryService = orderQueryService;
    }

    @GetMapping("/orders/ask")
    OrderAnswer ask(@RequestParam String question) {
        String sql = writeSql(question);
        List<Map<String, Object>> rows;
        try {
            rows = orderQueryService.run(sql);
        } catch (BadSqlGrammarException e) {
            // Give the model the database error once and let it fix its SQL
            sql = fixSql(question, sql, e.getSQLException().getMessage());
            rows = orderQueryService.run(sql);
        }
        return new OrderAnswer(question, sql, rows, explain(question, rows));
    }

    private String writeSql(String question) {
        String sql = SqlGuard.clean(sqlClient.prompt()
            .user(question)
            .call()
            .content());
        log.info("Generated SQL: {}", sql);
        return sql;
    }

    private String fixSql(String question, String sql, String error) {
        log.info("SQL failed: {}", error);
        String fixed = SqlGuard.clean(sqlClient.prompt()
            .user(u -> u.text("""
                Question: {question}
                Your SQL: {sql}
                The database returned this error: {error}
                Write the corrected SQL statement.""")
                .param("question", question)
                .param("sql", sql)
                .param("error", error))
            .call()
            .content());
        log.info("Corrected SQL: {}", fixed);
        return fixed;
    }

    private String explain(String question, List<Map<String, Object>> rows) {
        return supportClient.prompt()
            .user(u -> u.text("""
                Answer the question in one or two sentences using only these database rows.
                Question: {question}
                Rows: {rows}""")
                .param("question", question)
                .param("rows", rows.toString()))
            .call()
            .content();
    }

    // SqlGuard rejected the statement: answer 400 Bad Request with the reason
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    Map<String, String> rejected(IllegalArgumentException e) {
        return Map.of("error", e.getMessage());
    }

    record OrderAnswer(String question, String sql, List<Map<String, Object>> rows,
                       String answer) {
    }
}
