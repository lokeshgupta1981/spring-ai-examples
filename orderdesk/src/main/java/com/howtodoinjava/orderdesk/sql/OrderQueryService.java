package com.howtodoinjava.orderdesk.sql;

import java.sql.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderQueryService {

    private final JdbcTemplate jdbcTemplate;

    OrderQueryService(JdbcTemplate jdbcTemplate) {
        // A copy of the JdbcTemplate that returns at most 50 rows
        this.jdbcTemplate = new JdbcTemplate(jdbcTemplate.getDataSource());
        this.jdbcTemplate.setMaxRows(50);
    }

    // readOnly = true makes the database connection read-only for this call
    @Transactional(readOnly = true)
    public List<Map<String, Object>> run(String sql) {
        SqlGuard.check(sql);
        return jdbcTemplate.queryForList(sql).stream()
            .map(OrderQueryService::withLocalDates)
            .toList();
    }

    // java.sql.Date becomes a timestamp in JSON; LocalDate prints as 2026-10-03
    private static Map<String, Object> withLocalDates(Map<String, Object> row) {
        Map<String, Object> copy = new LinkedHashMap<>(row);
        copy.replaceAll((column, value) -> value instanceof Date date ? date.toLocalDate() : value);
        return copy;
    }
}
