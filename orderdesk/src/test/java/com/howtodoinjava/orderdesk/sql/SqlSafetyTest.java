package com.howtodoinjava.orderdesk.sql;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.transaction.support.TransactionTemplate;

class SqlSafetyTest {

    @Test
    void guardAllowsOnlyOneSelect() {
        assertThatCode(() -> SqlGuard.check("SELECT city FROM orders")).doesNotThrowAnyException();
        assertThatThrownBy(() -> SqlGuard.check("DELETE FROM orders"))
            .hasMessageStartingWith("Only SELECT statements are allowed");
        assertThatThrownBy(() -> SqlGuard.check("SELECT 1 FROM orders; DROP TABLE orders"))
            .hasMessageStartingWith("Only one statement is allowed");
    }

    @Test
    void readOnlyTransactionRejectsChanges() {
        SimpleDriverDataSource dataSource = new SimpleDriverDataSource(
            new org.hsqldb.jdbc.JDBCDriver(), "jdbc:hsqldb:mem:safety", "SA", "");
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.execute("CREATE TABLE orders (order_id VARCHAR(10))");
        jdbcTemplate.update("INSERT INTO orders VALUES ('ORD-1031')");

        TransactionTemplate readOnly = new TransactionTemplate(
            new DataSourceTransactionManager(dataSource));
        readOnly.setReadOnly(true);

        // Even if a DELETE got past SqlGuard, the read-only connection refuses it
        assertThatThrownBy(() -> readOnly.executeWithoutResult(
            status -> jdbcTemplate.update("DELETE FROM orders")))
            .hasMessageContaining("read-only");
    }
}
